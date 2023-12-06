<template>
  <div style="margin: 4px 0;">
    <template v-for="(subfield, index) in field.properties">
      <component
        :is="ErrorFields[subfield.type]"
        :key="index"
        :snapshot="snapshot.properties[subfield.field]"
      />
    </template>
  </div>
</template>

<script>
import TranslateApiString from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export default {
  mixins: [
    TranslateApiString
  ],
  inject: {
    FormFieldClass: {
      default: () => DynamicFormField
    }
  },
  props: {
    snapshot: {
      type: Object,
      required: true
    }
  },
  computed: {
    field () {
      return this.snapshot.field
    },
    $v () {
      return this.snapshot.$v
    },
    ErrorFields () {
      return this.FormFieldClass.ErrorFields
    }
  },
  created () {
    // Register local components dynamically
    // We do this in the created() lifecycle hook because the components must be registered before mount
    Object.assign(this.$options.components, this.FormFieldClass.ErrorComponents)
  }
}
</script>
