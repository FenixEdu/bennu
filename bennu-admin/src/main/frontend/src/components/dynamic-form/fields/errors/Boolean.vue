<template>
  <p v-if="$v.$error">
    <strong>{{ translate(field.label) }}:</strong>
    <template v-if="$v.$params.required && !$v.required">
      {{ $t('dynamic-form.fields.boolean.errors.required') }}
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
