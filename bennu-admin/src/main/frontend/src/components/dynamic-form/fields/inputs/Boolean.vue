<template>
  <radio-group
    :required="field.required"
    :name="$attrs.name || field.field"
    :options="[true, false]"
    :label="translate(field.label)"
    :description="translate(field.description)"
    :invalid="$v.$error"
  >
    <template #option="{ option, index }">
      <input
        :id="`${$attrs.name || field.field}-${index}`"
        v-model="$v.$model"
        :name="$attrs.name || field.field"
        :value="option"
        type="radio"
        class="f-field__input"
      >
      <label
        :for="`${$attrs.name || field.field}-${index}`"
        class="f-field__label"
      >
        {{ option ? translate(field.labelYes) : translate(field.labelNo) }}
      </label>
    </template>
    <template
      v-if="field.tooltip"
      #tooltip
    >
      <p>{{ translate(field.tooltip) }}</p>
    </template>
    <template #error-message>
      <template v-if="$v.$params.required && !$v.required">
        {{ $t('dynamic-form.fields.boolean.errors.required') }}
      </template>
    </template>
  </radio-group>
</template>

<script>
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import RadioGroup from '@/components/utils/fields/RadioGroup.vue'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export const extend = superclass => class extends superclass {
  get validations () {
    const validations = { }
    if (this.field.required) {
      validations.required = requiredIf(() => this.exists())
    }
    return validations
  }

  get emptyValue () {
    return undefined
  }

  parseSubmitValue () {
    return typeof this.previousSubmitValue === 'boolean' ? this.previousSubmitValue : this.emptyValue
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'BooleanField',
  components: {
    RadioGroup
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
