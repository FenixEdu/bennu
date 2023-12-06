<template>
  <p v-if="$v.$error">
    <strong>{{ translate(field.label) }}:</strong>
    <template v-if="$v.$params.required && !$v.required">
      {{ $t('dynamic-form.fields.quantity.errors.required') }}
    </template>
    <template v-else-if="$v.$params.integer && !$v.integer">
      {{ $t('dynamic-form.fields.quantity.errors.integer') }}
    </template>
    <template v-else-if="$v.$params.minValue && !$v.minValue">
      {{ $t('dynamic-form.fields.quantity.errors.min-value', { min: $v.$params.minValue.min }) }}
    </template>
    <template v-else-if="$v.$params.maxValue && !$v.maxValue">
      {{ $t('dynamic-form.fields.quantity.errors.max-value', { max: $v.$params.maxValue.max }) }}
    </template>
    <template v-else-if="$v.$params.maxDecimalPlaces && !$v.maxDecimalPlaces">
      {{ $tc('dynamic-form.fields.quantity.errors.max-decimal-places', $v.$params.maxDecimalPlaces.max, { max: $v.$params.maxDecimalPlaces.max }) }}
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
