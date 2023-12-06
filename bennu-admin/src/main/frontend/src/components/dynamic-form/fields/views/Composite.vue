<template>
  <div style="margin: 4px 0;">
    <template v-for="(subfield, index) in snapshot.field.properties">
      <component
        :is="ViewFields[subfield.type]"
        v-if="snapshot.properties[subfield.field].exists"
        :key="index"
        :snapshot="snapshot.properties[subfield.field]"
      />
    </template>
  </div>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export default {
  name: 'CompositeField',
  mixins: [
    TranslateApiStringMixin
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
    ViewFields () {
      return this.FormFieldClass.ViewFields
    }
  },
  created () {
    // Register local components dynamically
    // We do this in the created() lifecycle hook because the components must be registered before mount
    Object.assign(this.$options.components, this.FormFieldClass.ViewComponents)
  }
}
</script>
