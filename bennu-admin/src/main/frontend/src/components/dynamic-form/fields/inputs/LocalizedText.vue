<template>
  <div class="f-field">
    <fieldset :aria-describedby="[field.description ? `${id}-description` : undefined].concat($attrs['aria-describedby']).join(' ')">
      <legend class="f-field__label">
        {{ translate(field.label) }}
      </legend>
      <p
        v-if="field.description"
        :id="`${id}-description`"
        class="f-field__description"
      >
        {{ translate(field.description) }}
      </p>
      <div
        v-if="field.tooltip"
        class="f-field__tooltip"
      >
        <tooltip>
          <template #message>
            <p>{{ translate(field.tooltip) }}</p>
          </template>
          <template #trigger="{ attrs, events }">
            <!-- TODO: add aria-label to default button -->
            <button
              v-bind="attrs"
              v-on="events"
            >
              i
            </button>
          </template>
        </tooltip>
      </div>
      <template v-for="locale in field.locales">
        <!-- eslint-disable vue/no-mutating-props -->
        <component
          :is="field.multiline ? 'TextArea': 'TextInput'"
          :key="locale"
          v-model="formField.$v[locale].$model"
          :required="field.required"
          :name="($attrs.name || field.field) + '-' + locale"
          :label="$te(`label.${locale}`) ? $t(`label.${locale}`) : $t('label.fallback', { locale })"
          :placeholder="translate(field.example)"
          variant="outlined"
          :invalid="formField.$v[locale].$error"
          :type="inputType"
        >
          <!-- eslint-enable vue/no-mutating-props -->
          <template #error-message>
            <template v-if="formField.$v[locale].$params.required && !formField.$v[locale].required">
              {{ $t('dynamic-form.fields.text.errors.required') }}
            </template>
            <template v-else-if="formField.$v[locale].$params.email && !formField.$v[locale].email">
              {{ $t('dynamic-form.fields.text.errors.email') }}
            </template>
            <template v-else-if="formField.$v[locale].$params.url && !formField.$v[locale].url">
              {{ $t('dynamic-form.fields.text.errors.url') }}
            </template>
          </template>
        </component>
      </template>
    </fieldset>
  </div>
</template>

<script>
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import email from 'vuelidate/lib/validators/email'
import url from 'vuelidate/lib/validators/url'
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export const extend = superclass => class extends superclass {
  get validations () {
    const validations = {}
    this.field.locales.forEach(locale => {
      validations[locale] = {}
      if (this.field.required) {
        validations[locale].required = requiredIf(() => this.exists())
      }
      if (this.field.inputType === 'email') {
        validations[locale].email = email
      } else if (this.field.inputType === 'url') {
        validations[locale].url = url
      }
    })
    return validations
  }

  parseSubmitValue () {
    return !this.previousSubmitValue ? this.emptyValue : this.previousSubmitValue
  }

  get emptyValue () {
    return Object.fromEntries(this.field.locales.map(locale => [locale, '']))
  }
}

export const FormField = extend(DynamicFormField)

export const sharedMessages = {
  pt: {
    label: {
      'pt-PT': 'Versão em Português (pt-PT)',
      'en-GB': 'Versão em Inglês (en-GB)',
      fallback: 'Versão em {locale}'
    }
  },
  en: {
    label: {
      'pt-PT': 'Portuguese version (pt-PT)',
      'en-GB': 'English version (en-GB)',
      fallback: 'Version for locale “{locale}”'
    }
  }
}

export default {
  name: 'LocalizedTextField',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue'),
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
    id () {
      return `localized-text-${this.$attrs.name || this.field.field}`
    },
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
    }
  },
  i18n: {
    sharedMessages
  }
}
</script>
