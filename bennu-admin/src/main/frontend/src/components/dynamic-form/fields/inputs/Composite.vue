<template>
  <div class="composite__container">
    <div
      v-for="(subFormField, index) in formField.properties"
      v-show="subFormField.exists()"
      :key="index"
      class="composite__wrapper"
    >
      <readonly-field-wrapper
        :form-field="subFormField"
        :name="`${$attrs.name || field.field}-${subFormField.field.field}`"
        class="composite__field"
      />
    </div>
  </div>
</template>

<script>
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'
import ReadonlyFieldWrapper from '@/components/dynamic-form/fields/inputs/helpers/ReadonlyFieldWrapper.vue'

export const extend = superclass => class extends superclass {
  constructor ({ field, submitValue, uniqueId, location, form, parent }, options) {
    super({ field, submitValue, uniqueId, location, form, parent }, options)
    this.extraArgs = options

    /** @type {Array<DynamicFormField>} */
    this.properties = field.properties.map(
      subfield => this.constructor.getInstance({
        field: subfield,
        submitValue: submitValue?.[subfield.field],
        uniqueId: `${this.uniqueId}-${subfield.field}`,
        location: this.location,
        form,
        parent: this
      }, this.extraArgs)
    )
  }

  get emptyValue () {
    return Object.fromEntries(
      this.properties.map(subFormField => [subFormField.field.field, subFormField.emptyValue])
    )
  }

  isEmpty () {
    return this.properties.every(subFormField => subFormField.isEmpty())
  }

  get validations () {
    return Object.fromEntries(
      this.properties.map(subFormField => [subFormField.field.field, subFormField.validations])
    )
  }

  setVuelidate (vuelidate, reset) {
    super.setVuelidate(vuelidate, reset)
    this.properties.forEach(subFormField => subFormField.setVuelidate(this.$v?.[subFormField.field.field], reset))
  }

  parseSubmitValue () {
    return Object.fromEntries(
      this.properties.map(subFormField => [subFormField.field.field, subFormField.parseSubmitValue()])
    )
  }

  get value () {
    return Object.fromEntries(this.properties.map(subFormField => [subFormField.field.field, subFormField.value]))
  }

  get simpleValue () {
    return Object.fromEntries(this.properties.map(subFormField => [subFormField.field.field, subFormField.simpleValue]))
  }

  async getSubmitBlobs () {
    if (!this.exists()) {
      return {}
    }
    const submitBlobs = {}
    for (const subFormField of this.properties) {
      const fieldBlobs = await subFormField.getSubmitBlobs()
      Object.entries(fieldBlobs || {})
        .forEach(([key, blob]) => {
          submitBlobs[`.${subFormField.field.field}${key}`] = blob
        })
    }
    return submitBlobs
  }

  async getSubmitValue () {
    if (!this.exists()) {
      return undefined
    }
    const submitValue = {}
    for (const subFormField of this.properties) {
      submitValue[subFormField.field.field] = await subFormField.getSubmitValue()
    }
    return submitValue
  }

  setPreviousSubmitValue (value) {
    this.previousSubmitValue = value
    this.properties.forEach(subFormField => subFormField.setPreviousSubmitValue(value[subFormField.field.field]))
  }

  findField ({ name, pivot }) {
    for (const subFormField of this.properties) {
      const field = subFormField.findField({ name, pivot: this })
      if (field) return field
    }
    return super.findField({ name, pivot: this })
  }

  takeSnapshot () {
    const { snapshot, destroy } = super.takeSnapshot()
    const properties = {}
    const destroyers = []
    for (const formField of this.properties) {
      const { snapshot: fieldSnapshot, destroy: destroyField } = formField.takeSnapshot()
      properties[formField.field.field] = fieldSnapshot
      destroyers.push(destroyField)
    }

    return {
      snapshot: {
        ...snapshot,
        properties
      },
      destroy () {
        return Promise.all([
          destroy(),
          ...destroyers.map(d => d())
        ])
      }
    }
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'CompositeField',
  components: {
    ReadonlyFieldWrapper
  },
  props: {
    formField: {
      type: DynamicFormField,
      required: true
    }
  },
  computed: {
    field () {
      return this.formField.field
    },
    $v () {
      return this.formField.$v
    }
  }
}
</script>
<style lang="scss" scoped>
@import "@/assets/scss/variables";
.composite__container {
  display: flex;
  flex-direction: column;
  flex: 1;
  margin: -1.5rem !important;

  .composite__wrapper {
    padding-right: 1.5rem;
    padding-bottom: 1rem;
    padding-top: 1rem;

    & + & {
      margin-top: 2rem;
    }
  }

  .composite__field {
    padding-left: 1.5rem;
    margin-right: 1.5rem;
    box-sizing: border-box;
    position: relative;
  }

  @media screen and (min-width: 60rem) {
    display: flex;
    flex-flow: row wrap;
    flex: 1;

    .composite__wrapper {
      flex: 1;
      min-width: 35%;
      border-bottom: 1px solid $light-gray;

      + & {
        margin-top: 0;
      }
    }

    .composite__wrapper:nth-child(even) {
      border-left: 1px solid $light-gray;
    }
    .composite__wrapper:last-child,
    .composite__wrapper:nth-last-child(2):nth-child(odd) {
      border-bottom: none;
    }
  }
}

</style>
