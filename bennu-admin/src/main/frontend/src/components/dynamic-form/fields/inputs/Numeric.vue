<template>
  <text-input
    v-model="input"
    :required="field.required"
    :name="$attrs.name || field.field"
    :label="translate(field.label)"
    :description="translate(field.description)"
    :placeholder="translate(field.example)"
    variant="outlined"
    :invalid="$v.$error"
    inputmode="numeric"
  >
    <template
      v-if="field.tooltip"
      #tooltip
    >
      <p>{{ translate(field.tooltip) }}</p>
    </template>
    <template #error-message>
      <template v-if="$v.$params.required && !$v.required">
        {{ $t('dynamic-form.fields.numeric.errors.required') }}
      </template>
      <template v-if="$v.$params.numeric && !$v.numeric">
        {{ $t('dynamic-form.fields.numeric.errors.numeric') }}
      </template>
    </template>
  </text-input>
</template>

<script>
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import numeric from 'vuelidate/lib/validators/numeric'
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import TextInput from '@/components/utils/fields/TextInput.vue'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export const extend = superclass => class extends superclass {
  get validations () {
    const validations = { numeric }
    if (this.field.required) {
      validations.required = requiredIf(() => this.exists())
    }
    return validations
  }

  get emptyValue () {
    return ''
  }

  parseSubmitValue () {
    return /^[0-9]+$/.test(String(this.previousSubmitValue)) ? String(this.previousSubmitValue) : this.emptyValue
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'NumericField',
  components: {
    TextInput
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
  data () {
    return { input: this.formField.$v.$model }
  },
  computed: {
    field () {
      return this.formField.field
    },
    $v () {
      return this.formField.$v
    }
  },
  watch: {
    input (newValue) {
      this.$v.$model = newValue
    }
  }
}
</script>
