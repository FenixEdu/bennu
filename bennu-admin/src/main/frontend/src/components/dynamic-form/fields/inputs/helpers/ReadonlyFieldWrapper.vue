<template>
  <component
    :is="FormFields[field.type]"
    v-if="!readonly"
    :form-field="formField"
    v-bind="$attrs"
  />
  <div
    v-else
    class="field--readonly"
  >
    <span class="label label--sm label--primary label--muted">
      <span class="sr-only">(</span>
      {{ $t('readonly') }}
      <span class="sr-only">)</span>
    </span>
    <component
      :is="ViewFields[field.type]"
      :snapshot="snapshot"
      v-bind="$attrs"
    />
  </div>
</template>

<script>
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export default {
  provide () {
    return {
      FormFieldClass: this.formField.form.FormFieldClass
    }
  },
  props: {
    formField: {
      type: DynamicFormField,
      required: true
    }
  },
  data () {
    return {
      snapshot: undefined,
      destroySnapshot: undefined
    }
  },
  computed: {
    FormFields () {
      const FormFieldClass = this.formField.form.FormFieldClass
      return FormFieldClass.FormFields
    },
    ViewFields () {
      const FormFieldClass = this.formField.form.FormFieldClass
      return FormFieldClass.ViewFields
    },
    field () {
      return this.formField.field
    },
    readonly () {
      return this.formField.isReadonly()
    }
  },
  watch: {
    readonly: {
      immediate: true,
      handler (newValue) {
        if (newValue) {
          const { snapshot, destroy } = this.formField.takeSnapshot()
          this.snapshot = snapshot
          this.destroySnapshot = destroy
        }
      }
    }
  },
  created () {
    // Register local components dynamically
    // (only View components, Form components are registered globally at DynamicForm.vue)
    // We do this in the created() lifecycle hook because the components must be registered before mount
    const FormFieldClass = this.formField.form.FormFieldClass
    Object.assign(this.$options.components, FormFieldClass.ViewComponents)
  },
  async beforeDestroy () {
    if (this.destroySnapshot) {
      await this.destroySnapshot()
    }
  },
  i18n: {
    messages: {
      pt: {
        readonly: 'Não editável'
      },
      en: {
        readonly: 'Not editable'
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.field--readonly {
  width: 100%;

  .label {
    float: right;
    margin-left: 0.5rem;
  }
}
</style>
