<template>
  <div>
    <template v-for="(page, pageIndex) in pages">
      <div
        v-for="(section, sectionIndex) in page.sections"
        :key="`section-${pageIndex}-${sectionIndex}`"
        class="card"
      >
        <div
          class="card-row"
          :class="{'card-row--sm': !!$scopedSlots['form-section-meta']}"
        >
          <div class="card-row__text">
            <h3 class="h4 card-row__title">
              {{ translate(section.title) }}
            </h3>
          </div>
          <div
            v-if="!!$scopedSlots['form-section-meta']"
            class="card-row__meta"
          >
            <slot
              name="form-section-meta"
              :page="page"
              :page-index="pageIndex"
              :section="section"
              :section-index="sectionIndex"
            />
          </div>
        </div>
        <template v-for="(field, index) in section.properties">
          <div
            v-if="snapshot[pageIndex][sectionIndex][field.field].exists"
            :key="index"
            class="card-row card-row--sm"
          >
            <div class="card-row__text">
              <component
                :is="ViewFields[field.type]"
                :snapshot="snapshot[pageIndex][sectionIndex][field.field]"
              />
            </div>
          </div>
        </template>
      </div>
    </template>
  </div>
</template>

<script>
import { DynamicForm } from '@/components/dynamic-form/utils/form'
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { validationMixin } from 'vuelidate'

export default {
  mixins: [
    TranslateApiStringMixin,
    validationMixin
  ],
  provide () {
    return {
      FormFieldClass: this.dynamicForm.FormFieldClass
    }
  },
  validations () {
    return {
      form: this.dynamicForm.getValidations()
    }
  },
  props: {
    dynamicForm: {
      type: DynamicForm,
      required: true
    }
  },
  data () {
    return {
      form: this.dynamicForm.form,
      snapshot: undefined,
      destroySnapshot: undefined
    }
  },
  computed: {
    ViewFields () {
      return this.dynamicForm.FormFieldClass.ViewFields
    },
    pages () {
      return this.dynamicForm.structure.pages
    }
  },
  created () {
    this.dynamicForm.setVuelidate(this.$v.form, this.dynamicForm.$v === undefined)

    // Register local components dynamically
    // We do this in the created() lifecycle hook because the components must be registered before mount
    const FormFieldClass = this.dynamicForm.FormFieldClass
    Object.assign(this.$options.components, FormFieldClass.ViewComponents)

    const { snapshot, destroy } = this.dynamicForm.takeSnapshot()
    this.snapshot = snapshot
    this.destroySnapshot = destroy
  },
  async beforeDestroy () {
    if (this.destroySnapshot) {
      await this.destroySnapshot()
    }
  }
}
</script>
