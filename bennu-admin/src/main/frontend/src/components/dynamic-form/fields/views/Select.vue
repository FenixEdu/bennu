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
    <template v-else-if="optionsLabels">
      {{ optionsLabels }}
    </template>
    <template v-else>
      {{ value }}
    </template>
  </p>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'

export default {
  name: 'SelectField',
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
    },
    optionsLabels () {
      if (this.isEmpty) {
        return undefined
      } else if (this.field.multiple) {
        return this.value.map(option => this.translate(option.label)).join(', ')
      } else {
        return this.translate(this.value.label)
      }
    }
  }
}
</script>
