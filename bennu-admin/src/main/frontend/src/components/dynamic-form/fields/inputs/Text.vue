<template>
  <!-- FIXME: -->
  <!-- eslint-disable vue/no-mutating-props -->
  <component
    :is="field.multiline ? 'TextArea': 'TextInput'"
    v-model="formField.$v.$model"
    :required="field.required"
    :name="$attrs.name || field.field"
    :label="translate(field.label)"
    :description="translate(field.description)"
    :placeholder="translate(field.example)"
    variant="outlined"
    :invalid="formField.$v.$error"
    :type="inputType"
  >
    <template
      v-if="field.maxLength"
      #length
    >
      {{ maxLengthText }}
    </template>
    <!-- eslint-enable vue/no-mutating-props -->
    <template
      v-if="field.tooltip"
      #tooltip
    >
      <p>{{ translate(field.tooltip) }}</p>
    </template>
    <template #error-message>
      <template v-if="formField.$v.$params.required && !formField.$v.required">
        {{ $t('dynamic-form.fields.text.errors.required') }}
      </template>
      <template v-else-if="formField.$v.$params.email && !formField.$v.email">
        {{ $t('dynamic-form.fields.text.errors.email') }}
      </template>
      <template v-else-if="formField.$v.$params.url && !formField.$v.url">
        {{ $t('dynamic-form.fields.text.errors.url') }}
      </template>
      <template v-else-if="formField.$v.$params.maxLength && !formField.$v.maxLength">
        {{ $tc('dynamic-form.fields.text.errors.max-length', formField.$v.$params.maxLength.max, { max: formField.$v.$params.maxLength.max }) }}
      </template>
    </template>
  </component>
</template>

<script>
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import email from 'vuelidate/lib/validators/email'
import url from 'vuelidate/lib/validators/url'
import maxLength from 'vuelidate/lib/validators/maxLength'
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export const extend = superclass => class extends superclass {
  get validations () {
    const validations = {}
    if (this.field.required) {
      validations.required = requiredIf(() => this.exists())
    }
    if (this.field.maxLength) {
      validations.maxLength = maxLength(this.field.maxLength)
    }
    if (this.field.inputType === 'email') {
      validations.email = email
    } else if (this.field.inputType === 'url') {
      validations.url = url
    }
    return validations
  }

  parseSubmitValue () {
    return !this.previousSubmitValue ? this.emptyValue : String(this.previousSubmitValue)
  }

  get emptyValue () {
    return ''
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'TextField',
  components: {
    TextInput: () => import('@/components/utils/fields/TextInput.vue'),
    TextArea: () => import('@/components/utils/fields/TextArea.vue')
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
    },
    inputType () {
      return this.field.inputType === 'email'
        ? 'email'
        : this.field.inputType === 'url'
          ? 'url'
          : undefined
    },
    maxLengthText () {
      return `${this.$v.$model?.length ?? 0}/${this.field.maxLength}`
    }
  }
}
</script>
