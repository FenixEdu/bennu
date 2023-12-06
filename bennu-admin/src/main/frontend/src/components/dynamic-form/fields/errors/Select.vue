<template>
  <p v-if="$v.$error">
    <strong>{{ translate(field.label) }}:</strong>
    <template v-if="$v.option.$params.required && !$v.option.required">
      {{ $t(`dynamic-form.fields.select.errors.${field.multiple ? 'multi-' : ''}required`) }}
    </template>
    <template v-else-if="$v.otherOption.$params.required && !$v.otherOption.required">
      {{ $t('dynamic-form.fields.text.errors.required') }}
    </template>
  </p>
</template>

<script>
import TranslateApiString from '@/mixins/TranslateApiString'

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
  }
}
</script>
