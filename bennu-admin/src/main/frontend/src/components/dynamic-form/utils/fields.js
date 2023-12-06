import isEqual from 'underscore/modules/isEqual'

export const FieldTypes = Object.freeze({
  LocalizedText: 'LocalizedTextField',
  Text: 'TextField',
  Quantity: 'QuantityField',
  Numeric: 'NumericField',
  Boolean: 'BooleanField',
  Select: 'SelectField',
  AsyncSelect: 'AsyncSelectField',
  DateTime: 'DateTimeField',
  File: 'FileField',
  Composite: 'CompositeField',
  Array: 'ArrayField'
})

function buildFieldTypes (prefix = '', fieldTypes) {
  return Object.freeze(
    Object.entries(fieldTypes).reduce((acc, entry) => {
      const [file, componentName] = entry
      acc[file] = prefix + componentName
      return acc
    }, {})
  )
}

export class DynamicFormField {
  constructor ({ field, submitValue, uniqueId = field.field, location, form, parent }) {
    this.field = field
    this.uniqueId = uniqueId
    this.location = location
    this.previousSubmitValue = submitValue
    this.form = form
    this.parent = parent
  }

  get value () {
    return this.$v?.$model
  }

  get simpleValue () {
    return this.value
  }

  exists () {
    if (!('onlyIf' in this.field)) return true

    const verifyOnlyIf = onlyIf => {
      const field = this.findField({ name: onlyIf.field })
      if (!field || !field.exists()) return false
      const conditioningValue = onlyIf.value
      const equalsConditioningValue = Array.isArray(conditioningValue)
        ? conditioningValue.some(value => isEqual(field.value, value))
        : isEqual(field.value, conditioningValue)
      const isFieldEmpty = field.value === undefined || isEqual(field.value, field.emptyValue)
      return (onlyIf.negation ?? false) ? (!isFieldEmpty && !equalsConditioningValue) : equalsConditioningValue
    }
    if (Array.isArray(this.field.onlyIf)) {
      return this.field.onlyIf.every(verifyOnlyIf)
    } else {
      return verifyOnlyIf(this.field.onlyIf)
    }
  }

  isReadonly () {
    return (this.field.readonly ?? false) || (this.parent?.isReadonly() ?? false)
  }

  isEmpty () {
    return this.value === undefined || isEqual(this.value, this.emptyValue)
  }

  setVuelidate (vuelidate, reset = false) {
    this.$v = vuelidate
    if (reset && vuelidate !== undefined) {
      this.$v.$model = this.parseSubmitValue(this.previousSubmitValue)
    }
  }

  static get FormComponentsPrefix () {
    return 'Form'
  }

  static get ErrorComponentsPrefix () {
    return 'Error'
  }

  static get ViewComponentsPrefix () {
    return 'View'
  }

  static buildFieldImports (fieldTypes, importComponent) {
    return Object.entries(fieldTypes).reduce((acc, entry) => {
      const [file, componentName] = entry
      acc[componentName] = importComponent(file)
      return acc
    }, {})
  }

  static getFieldClass (field) {
    const component = require(`@/components/dynamic-form/fields/inputs/${field.type}`)
    if (!Object.prototype.hasOwnProperty.call(component, 'FormField')) {
      console.warn(`The component "${field.type}" does not export the class "FormField"`)
    }
    return component.FormField
  }

  /**
   * @param {String} type The component type (e.g. "Text", "Boolean", ...)
   * @returns {Promise<any>}
   */
  static importFormComponent (type) {
    return import('@/components/dynamic-form/fields/inputs/' + type + '.vue') // string interpolation breaks import()
  }

  /**
   * @param {String} type The component type (e.g. "Text", "Boolean", ...)
   * @returns {Promise<any>}
   */
  static importErrorComponent (type) {
    return import('@/components/dynamic-form/fields/errors/' + type + '.vue') // string interpolation breaks import()
  }

  /**
   * @param {String} type The component type (e.g. "Text", "Boolean", ...)
   * @returns {Promise<any>}
   */
  static importViewComponent (type) {
    return import('@/components/dynamic-form/fields/views/' + type + '.vue') // string interpolation breaks import()
  }

  static get FormFields () {
    return buildFieldTypes(DynamicFormField.FormComponentsPrefix, FieldTypes)
  }

  static get ErrorFields () {
    return buildFieldTypes(DynamicFormField.ErrorComponentsPrefix, FieldTypes)
  }

  static get ViewFields () {
    return buildFieldTypes(DynamicFormField.ViewComponentsPrefix, FieldTypes)
  }

  static get FormComponents () {
    return this.buildFieldImports(
      this.FormFields,
      file => () => this.importFormComponent(file)
    )
  }

  static get ErrorComponents () {
    return this.buildFieldImports(
      this.ErrorFields,
      file => () => this.importErrorComponent(file)
    )
  }

  static get ViewComponents () {
    return this.buildFieldImports(
      this.ViewFields,
      file => () => this.importViewComponent(file)
    )
  }

  static getInstance ({ field, submitValue, uniqueId, location, form, parent }, options) {
    const FormField = this.getFieldClass(field)
    if (!FormField) {
      console.warn(`Using generic class (${this.name}) instead of custom FormField for "${field.name}" (${field.type}). This may produce unexpected results.`)
    }
    return FormField
      ? new FormField({ field, submitValue, uniqueId, location, form, parent }, options)
      : new this({ field, submitValue, uniqueId, location, form, parent }, options)
  }

  get validations () {
    console.warn(`Using default validations (empty object) for "${this.field.field}" (${this.field.type}). This may produce unexpected results.`)
    return {}
  }

  get emptyValue () {
    console.warn(`Using default empty value (undefined) for "${this.field.field}" (${this.field.type}). This may produce unexpected results.`)
    return undefined
  }

  parseSubmitValue () {
    return this.previousSubmitValue
  }

  async getSubmitBlobs () {
    return {}
  }

  getSubmitValue () {
    return this.exists() ? this.value : undefined
  }

  setPreviousSubmitValue (value) {
    this.previousSubmitValue = value
  }

  validate (value, $v = this.$v) {
    return $v.$flattenParams()
      .filter(rule => rule.name !== 'required')
      .map(rule => rule.path.length === 0
        ? $v[rule.name]
        : rule.path.reduce((acc, step) => acc[step], $v)?.[rule.name]
      )
      .every(validationResult => validationResult === true) // validation result is false when invalid
  }

  findField ({ name, pivot }) {
    if (pivot) {
      return name === this.field.field ? this : undefined
    } else {
      return this.form.findField({ name, pivot: this })
    }
  }

  getSiblingFields () {
    const { page: locationPage, section: locationSection, ...locationRest } = this.location
    const keys = Object.keys(locationRest)
    if (keys.length === 1) {
      // we are inside an array
      const parentField = keys[0]
      const parent = this.form.findField({ name: parentField, pivot: this })
      const properties = parent.field.properties
        // drill down inside composite fields
        .map(property => property?.properties ?? property)
        .flat()
        .filter(property => property.field !== this.field.field)
      return properties
        .map(p => this.findField({ name: p.field }))
        .filter(p => p !== undefined)
    }
    const { pages } = this.form.structure
    const page = pages[locationPage]
    const section = page.sections[locationSection]
    const properties = section.properties
      .map(property => property?.properties ?? property)
      .flat()
      .filter(property => property.field !== this.field.field)
    return properties
      .map(p => this.form.findField({ name: p.field, pivot: this }))
      .filter(p => p !== undefined)
  }

  takeSnapshot () {
    return {
      snapshot: {
        field: JSON.parse(JSON.stringify(this.field)),
        value: this.value,
        $v: JSON.parse(JSON.stringify(this.$v)),
        exists: this.exists(),
        isEmpty: this.isEmpty()
      },
      destroy () {}
    }
  }
}

/**
 * @callback ImportComponentCallback
 * @param {string} fieldType
 * @returns {Promise<import('vue').Component>}
 */
/**
 * @callback GetFieldClassCallback
 * @param {String} field - An object representing the field as per spec
 * @returns {any}
 */
/**
 * @callback InitCallback
 * @param {...any} args - Extra arguments
 */
/**
 * @param {Object} param0
 * @param {InitCallback} param0.init - Initialization called in constructor
 * @param {GetFieldClassCallback} param0.getFieldClass
 * @param {{ [type: string]: string }} param0.fieldTypes
 * @param {ImportComponentCallback} param0.importFormComponent
 * @param {ImportComponentCallback} param0.importErrorComponent
 * @param {ImportComponentCallback} param0.importViewComponent
 */
export function extendFormField ({
  init, getFieldPath, getFieldClass,
  importFormComponent, importViewComponent, importErrorComponent,
  fieldTypes = FieldTypes
}) {
  return class FormField extends DynamicFormField {
    constructor ({ field, submitValue, uniqueId, location, form }, options) {
      super({ field, submitValue, uniqueId, location, form }, options)
      if (init) {
        init.call(this, options)
      }
    }

    static get FormFields () {
      return buildFieldTypes(DynamicFormField.FormComponentsPrefix, fieldTypes ?? FieldTypes)
    }

    static get ViewFields () {
      return buildFieldTypes(DynamicFormField.ViewComponentsPrefix, fieldTypes ?? FieldTypes)
    }

    static get ErrorFields () {
      return buildFieldTypes(DynamicFormField.ErrorComponentsPrefix, fieldTypes ?? FieldTypes)
    }

    static get FormComponents () {
      return this.buildFieldImports(
        this.FormFields,
        file => () => (importFormComponent?.call(this, file) ?? this.importFormComponent(file))
      )
    }

    static get ErrorComponents () {
      return this.buildFieldImports(
        this.ErrorFields,
        file => () => (importErrorComponent?.call(this, file) ?? this.importErrorComponent(file))
      )
    }

    static get ViewComponents () {
      return this.buildFieldImports(
        this.ViewFields,
        file => () => (importViewComponent?.call(this, file) ?? this.importViewComponent(file))
      )
    }

    static getFieldClass (field) {
      try {
        return getFieldClass.call(this, field)
      } catch (err) {
        const base = require(`@/components/dynamic-form/fields/inputs/${field.type}`)
        return class extends base.extend(FormField) {
          constructor (args, options) {
            super(args, options)
            if (init) {
              init.call(this, options)
            }
          }
        }
      }
    }
  }
}
