<template>
  <p v-if="$v.$error">
    <strong>{{ translate(field.label) }}:</strong>
    <template v-if="$v.date && !$v.date.isValid">
      {{ $t('dynamic-form.fields.datetime.errors.date') }}
    </template>
    <template v-if="$v.date && $v.date.$params.required && !$v.date.required">
      {{ $t('dynamic-form.fields.datetime.errors.required') }}
    </template>
    <template v-else-if="$v.time && !$v.time.isValid">
      {{ $t('dynamic-form.fields.datetime.errors.time') }}
    </template>
    <template v-else-if="$v.time && $v.time.$params.required && !$v.time.required">
      {{ $t('dynamic-form.fields.datetime.errors.required') }}
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
