<template>
  <p>
    <strong>{{ translate(field.label) }}:</strong>
    <ul class="l-list l-list--bulleted">
      <li
        v-for="locale in field.locales"
        :key="locale"
        :class="{ 'u-text-multiline': field.multiline }"
        class="l-list__item"
      >
        <strong>{{ $te(`label.${locale}`) ? $t(`label.${locale}`) : $t('label.fallback', { locale }) }}:</strong>
        <template
          v-if="isEmpty || value[locale] === undefined"
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
          {{ value[locale] }}
        </template>
      </li>
    </ul>
  </p>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { sharedMessages } from '../inputs/LocalizedText.vue'

export default {
  name: 'LocalizedTextField',
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
  },
  i18n: {
    sharedMessages
  }
}
</script>
