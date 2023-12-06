<template>
  <div
    v-if="$v.$error"
    style="margin: 4px 0;"
  >
    <p
      v-for="locale in field.locales"
      :key="locale"
    >
      <strong>{{ translate(field.label) }} – {{ $te(`label.${locale}`) ? $t(`label.${locale}`) : $t('label.fallback', { locale }) }}:</strong>
      <template v-if="$v[locale].$params.required && !$v[locale].required">
        {{ $t('dynamic-form.fields.text.errors.required') }}
      </template>
    </p>
  </div>
</template>

<script>
import TranslateApiString from '@/mixins/TranslateApiString'
import { sharedMessages } from '../inputs/LocalizedText.vue'

export default {
  mixins: [
    TranslateApiString
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
    $v () {
      return this.snapshot.$v
    }
  },
  i18n: {
    sharedMessages
  }
}
</script>
