<template>
  <p>
    <strong>{{ translate(field.label) }}:</strong>
    <template v-if="isEmpty">
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
      {{ value }}
    </template>
  </p>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'

export default {
  name: 'NumericField',
  mixins: [
    TranslateApiStringMixin
  ],
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
    value () {
      return this.snapshot.value
    },
    isEmpty () {
      return this.snapshot.isEmpty
    }
  }
}
</script>
