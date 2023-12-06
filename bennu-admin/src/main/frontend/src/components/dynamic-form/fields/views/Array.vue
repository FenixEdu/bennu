<template>
  <p>
    <strong>{{ translate(field.label) }}:</strong>
    <template
      v-if="isEmpty"
    >
      <span class="sr-only">
        {{ $t('dynamic-form.fields.empty-state.aria-label') }}
      </span>
      <span
        aria-hidden="true"
        class="u-text-secondary"
      >
        {{ $t('dynamic-form.fields.empty-state.text') }}
      </span>
    </template>
    <template v-else>
      <ol class="l-list l-list--bulleted">
        <li
          v-for="item in snapshot.items"
          :key="item.key"
          class="l-list__item"
        >
          <template v-for="fieldSnapshot in item.properties">
            <component
              :is="ViewFields[fieldSnapshot.field.type]"
              v-if="fieldSnapshot.exists"
              :key="fieldSnapshot.field.field"
              :snapshot="fieldSnapshot"
            />
          </template>
        </li>
      </ol>
    </template>
  </p>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export default {
  name: 'ArrayField',
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
    },
    isEmpty () {
      return this.snapshot.isEmpty
    },
    field () {
      return this.snapshot.field
    }
  },
  created () {
    // Register local components dynamically
    // We do this in the created() lifecycle hook because the components must be registered before mount
    Object.assign(this.$options.components, this.FormFieldClass.ViewComponents)
  }
}
</script>
