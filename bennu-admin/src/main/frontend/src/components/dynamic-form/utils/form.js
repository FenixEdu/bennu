import { DynamicFormField } from './fields'

export function getFormTreeVisitor (structure) {
  return function (visitor) {
    const { pages = [] } = structure
    const promises = [] // if visitor is async, it collects all the promises
    for (let pageIndex = 0; pageIndex < pages.length; pageIndex++) {
      const page = pages[pageIndex]
      for (let sectionIndex = 0; sectionIndex < page.sections.length; sectionIndex++) {
        const section = page.sections[sectionIndex]
        for (const field of section.properties) {
          promises.push(visitor(field, pageIndex, sectionIndex))
        }
      }
    }
    return promises
  }
}

export function getFormTreeBuilder (structure, anotherFormTree) {
  return function (getFieldValue) {
    const form = {}
    const visit = getFormTreeVisitor(structure)
    visit((field, pageIndex, sectionIndex) => {
      if (!form[pageIndex]) form[pageIndex] = {}
      if (!form[pageIndex][sectionIndex]) form[pageIndex][sectionIndex] = {}

      const fieldData = anotherFormTree?.[pageIndex]?.[sectionIndex]?.[field.field]
      form[pageIndex][sectionIndex][field.field] = getFieldValue(field, fieldData, pageIndex, sectionIndex)
    })
    return form
  }
}

export class DynamicForm {
  constructor (structure, data, options) {
    /** The original form-data structure, as fetched from the backend (extra pages may be appended) */
    this.structure = structure ?? { pages: [] }
    this.structure = 'pages' in structure ? structure : { ...structure, pages: [] }
    /** The original form-data values, as fetched from the backend */
    this.data = data ?? {}

    this.setupExtraPages(options)

    /** The tree structure containing the ApplicationFormField classes representing the fields */
    this.tree = this.getTree(this.structure, this.data, options)
    /** The tree structure containing form values as they feature on the UI (used by vuelidate, not the final values submitted) */
    this.form = this.getEmptyForm(this.structure, this.tree, options)
  }

  get FormFieldClass () {
    return DynamicFormField
  }

  get size () {
    return this.structure.pages.length
  }

  setupExtraPages () {
    // intentionally left blank
  }

  pushPage (pageId, pageStructure, pageData) {
    if (this.findPage(pageId) === undefined) {
      pageStructure.pageId = pageId
      this.structure.pages.push(pageStructure)
    }
    const length = this.structure.pages.length
    this.data[length - 1] = pageData
    return length
  }

  findPage (pageId) {
    return this.structure.pages.find(page => page.pageId === pageId)
  }

  splitPageData (pageId) {
    const pageIndex = this.structure.pages.findIndex(page => page.pageId === pageId)
    if (pageIndex > -1) {
      const { [pageIndex]: pageData, ...rest } = this.data
      return { pageData, rest }
    }
    return { rest: this.data }
  }

  getTree (structure, data, options) {
    const builder = getFormTreeBuilder(structure, data)
    return builder((field, value, pageIndex, sectionIndex) =>
      this.FormFieldClass.getInstance({
        field,
        submitValue: value,
        uniqueId: field.field,
        location: { page: pageIndex, section: sectionIndex },
        form: this
      }, options)
    )
  }

  setVuelidate (vuelidate, reset = false) {
    this.$v = vuelidate
    const visit = getFormTreeVisitor(this.structure, this.tree)
    visit((field, pageIndex, sectionIndex) => {
      const formField = this.tree[pageIndex][sectionIndex][field.field]
      formField.setVuelidate(this.$v[pageIndex][sectionIndex][field.field], reset)
    })
    if (reset) {
      this.$v.$reset()
    }
  }

  getEmptyForm (structure, fieldTree) {
    const builder = getFormTreeBuilder(structure, fieldTree)
    return builder(
      (field, formField) => formField.emptyValue
    )
  }

  getValidations (structure = this.structure, fieldTree = this.tree) {
    const builder = getFormTreeBuilder(structure, fieldTree)
    return builder(
      (field, formField) => formField.validations
    )
  }

  /* Binary blobs to submit */
  async getSubmitBlobs () {
    const blobs = {}
    const visit = getFormTreeVisitor(this.structure)

    await Promise.all(
      visit(
        async (field, pageIndex, sectionIndex) => {
          const formField = this.tree[pageIndex][sectionIndex][field.field]
          const value = await formField.getSubmitBlobs()
          Object.entries(value || {})
            .forEach(([key, blob]) => {
              blobs[`${pageIndex}.${sectionIndex}.${field.field}${key}`] = blob
            })
        }
      )
    )
    return blobs
  }

  async getSubmitData () {
    const visit = getFormTreeVisitor(this.structure)
    // get json submit data
    const data = {}
    await Promise.all(
      visit(
        async (field, pageIndex, sectionIndex) => {
          const formField = this.tree[pageIndex][sectionIndex][field.field]
          if (!data[pageIndex]) data[pageIndex] = {}
          if (!data[pageIndex][sectionIndex]) data[pageIndex][sectionIndex] = {}
          data[pageIndex][sectionIndex][field.field] = await formField.getSubmitValue()
        }
      )
    )
    return data
  }

  setSubmitted (data) {
    const visit = getFormTreeVisitor(this.structure)
    // update previousSubmitValue with final values from data
    visit(
      (field, pageIndex, sectionIndex) => {
        const formField = this.tree[pageIndex][sectionIndex][field.field]
        if (!data[pageIndex]) data[pageIndex] = {}
        if (!data[pageIndex][sectionIndex]) data[pageIndex][sectionIndex] = {}
        formField.setPreviousSubmitValue(data[pageIndex][sectionIndex][field.field])
      }
    )
    this.data = data
  }

  findField ({ name, pivot }) {
    const field = pivot.findField({ name, pivot })
    if (field) return field

    const { page: locationPage, section: locationSection, ...locationRest } = pivot.location
    if (Object.keys(locationRest).length > 0) {
      // pivot is within another field (e.g. Array)
      // we look for the field inside the containing field
      const sectionFields = this.tree[locationPage][locationSection]
      for (const fieldName in locationRest) {
        const formField = sectionFields[fieldName]
        const field = formField?.findField({ name, pivot })
        if (field) return field
      }
    }
    const { pages } = this.structure
    for (let pageIndex = 0; pageIndex < pages.length; pageIndex++) {
      const page = this.tree[pageIndex]
      for (let sectionIndex = 0; sectionIndex < pages[pageIndex].sections.length; sectionIndex++) {
        const section = page[sectionIndex]
        for (const fieldName in section) {
          const formField = section[fieldName]
          const field = formField.findField({ name, pivot })
          if (field) return field
        }
      }
    }
    return undefined
  }

  takeSnapshot () {
    const builder = getFormTreeBuilder(this.structure, this.tree)
    const destroyers = []
    const snapshot = builder(
      (field, formField) => {
        const { snapshot, destroy } = formField.takeSnapshot()
        destroyers.push(destroy)
        return snapshot
      }
    )
    const destroy = () => {
      return Promise.all(destroyers.map(d => d()))
    }
    return { snapshot, destroy }
  }
}
