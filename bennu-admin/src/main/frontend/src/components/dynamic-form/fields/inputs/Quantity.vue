<template>
  <number-input
    v-model="$v.$model"
    :required="field.required"
    :name="$attrs.name || field.field"
    :label="translate(field.label)"
    :description="translate(field.description)"
    :placeholder="translate(field.example)"
    variant="outlined"
    :invalid="$v.$error"
    :min="field.min"
    :max="field.max"
    :step="field.step"
    inputmode="decimal"
  >
    <template
      v-if="field.tooltip"
      #tooltip
    >
      <p>{{ translate(field.tooltip) }}</p>
    </template>
    <template #error-message>
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
    </template>
  </number-input>
</template>

<script>
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import minValue from 'vuelidate/lib/validators/minValue'
import maxValue from 'vuelidate/lib/validators/maxValue'
import integer from 'vuelidate/lib/validators/integer'
import decimal from 'vuelidate/lib/validators/decimal'
import { withParams, req } from 'vuelidate/lib/validators/common'

import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import NumberInput from '@/components/utils/fields/NumberInput.vue'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

const countDecimalPlaces = num => Math.floor(num) === num ? 0 : Number(num).toString().split('.')[1]?.length ?? 0
const maxDecimalPlaces = places => withParams(
  { type: 'maxDecimalPlaces', max: places },
  value => {
    const result = countDecimalPlaces(value)
    return !req(value) || (result <= places)
  }
)

export const extend = superclass => class extends superclass {
  get validations () {
    const validations = {}
    if (this.field.required) {
      validations.required = requiredIf(() => this.exists())
    }
    if (this.field.step !== undefined && countDecimalPlaces(this.field.step) > 0) {
      validations.decimal = decimal
      validations.maxDecimalPlaces = maxDecimalPlaces(countDecimalPlaces(this.field.step))
    } else {
      validations.integer = integer
    }
    if (this.field.min !== undefined) {
      validations.minValue = minValue(this.field.min)
    }
    if (this.field.max !== undefined) {
      validations.maxValue = maxValue(this.field.max)
    }
    return validations
  }

  get emptyValue () {
    return undefined
  }

  parseSubmitValue () {
    if (Number.isNaN(Number(this.previousSubmitValue))) {
      return this.emptyValue
    }
    return Number(this.previousSubmitValue)
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'QuantityField',
  components: {
    NumberInput
  },
  mixins: [
    TranslateApiStringMixin
  ],
  props: {
    formField: {
      type: DynamicFormField,
      required: true
    }
  },
  computed: {
    field () {
      return this.formField.field
    },
    $v () {
      return this.formField.$v
    }
  }
}
</script>
