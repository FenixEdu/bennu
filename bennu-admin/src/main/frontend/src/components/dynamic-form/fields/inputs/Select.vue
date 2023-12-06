<template>
  <select-with-other-wrapper
    :form-field="formField"
    v-bind="$attrs"
  >
    <template #default="{ attrs: selectWithOtherAttrs }">
      <checkbox-group
        v-if="field.multiple"
        ref="field"
        :required="!$v.$model.showOtherOption && field.required"
        :name="$attrs.name || field.field"
        :options="limitedOptions"
        :label="translate(field.label)"
        :description="translate(field.description)"
        :invalid="$v.option.$error"
        v-bind="selectWithOtherAttrs"
        tabindex="-1"
      >
        <template #option="{ option, index, attrs }">
          <input
            :id="`${$attrs.name || field.field}-${index}`"
            v-model="$v.option.$model"
            :name="$attrs.name || field.field"
            :value="option"
            type="checkbox"
            class="f-field__input"
            v-bind="attrs"
          >
          <label
            :for="`${$attrs.name || field.field}-${index}`"
            class="f-field__label"
          >
            {{ translate(option.label) }}
          </label>
        </template>
        <template
          v-if="field.tooltip"
          #tooltip
        >
          <p>{{ translate(field.tooltip) }}</p>
        </template>
        <template #error-message>
          <template v-if="$v.option.$params.required && !$v.option.required">
            {{ $t('dynamic-form.fields.select.errors.multi-required') }}
          </template>
        </template>
      </checkbox-group>
      <radio-group
        v-else-if="!field.collapse"
        ref="field"
        :required="!$v.$model.showOtherOption && field.required"
        :name="$attrs.name || field.field"
        :options="limitedOptions"
        :label="translate(field.label)"
        :description="translate(field.description)"
        :invalid="$v.option.$error"
        v-bind="selectWithOtherAttrs"
        tabindex="-1"
      >
        <template #option="{ option, index, attrs }">
          <input
            :id="`${$attrs.name || field.field}-${index}`"
            v-model="$v.option.$model"
            :name="$attrs.name || field.field"
            :value="option"
            type="radio"
            class="f-field__input"
            v-bind="attrs"
          >
          <label
            :for="`${$attrs.name || field.field}-${index}`"
            class="f-field__label"
          >
            {{ translate(option.label) }}
          </label>
        </template>
        <template
          v-if="field.tooltip"
          #tooltip
        >
          <p>{{ translate(field.tooltip) }}</p>
        </template>
        <template #error-message>
          <template v-if="$v.option.$params.required && !$v.option.required">
            {{ $t('dynamic-form.fields.select.errors.required') }}
          </template>
        </template>
      </radio-group>
      <select-input
        v-else-if="limitedOptions.length <= maxNonSearchableLength"
        ref="field"
        v-model="$v.option.$model"
        :required="!$v.$model.showOtherOption && field.required"
        :name="$attrs.name || field.field"
        :options="limitedOptions"
        :label="translate(field.label)"
        :description="translate(field.description)"
        :invalid="$v.option.$error"
        variant="outlined"
        v-bind="selectWithOtherAttrs"
      >
        <template #options="{ options }">
          <template v-for="(option, index) in options">
            <option
              :key="index"
              :value="option"
            >
              {{ translate(option.label) }}
            </option>
          </template>
        </template>
        <template
          v-if="field.tooltip"
          #tooltip
        >
          <p>{{ translate(field.tooltip) }}</p>
        </template>
        <template #error-message>
          <template v-if="$v.option.$params.required && !$v.option.required">
            {{ $t('dynamic-form.fields.select.errors.required') }}
          </template>
        </template>
      </select-input>
      <select-with-search
        v-else
        ref="field"
        v-model="$v.option.$model"
        :input-text.sync="searchInput"
        :required="!$v.$model.showOtherOption && field.required"
        :name="$attrs.name || field.field"
        :label="translate(field.label)"
        :description="translate(field.description)"
        :search-helper="searchHelper"
        :invalid="$v.option.$error"
        variant="outlined"
        v-bind="selectWithOtherAttrs"
      >
        <template #option="{ option }">
          {{ translate(option.label) }}
        </template>
        <template
          v-if="field.tooltip"
          #tooltip
        >
          <p>{{ translate(field.tooltip) }}</p>
        </template>
        <template #error-message>
          <template v-if="$v.option.$params.required && !$v.option.required">
            {{ $t('dynamic-form.fields.select.errors.required') }}
          </template>
        </template>
      </select-with-search>
    </template>
  </select-with-other-wrapper>
</template>

<script>
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import isEqual from 'underscore/modules/isEqual'

import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'
import { LocalizedString } from '@/i18n'
import SelectWithOtherWrapper from '@/components/dynamic-form/fields/inputs/helpers/AllowOtherWrapper.vue'

/** The maximum number of options of a collapsed select. If the number of
 * options exceeds this value, SelectWithSearch will be used instead of SelectInput
 */
export const MAX_NON_SEARCHABLE_LENGTH = 5

export const extend = superclass => class extends superclass {
  get validations () {
    const validations = { }
    if (this.field.required && this.field.allowOther) {
      validations.option = {
        required: requiredIf(model => this.exists() && !model.showOtherOption)
      }
      validations.otherOption = {
        required: requiredIf(model => this.exists() && model.showOtherOption)
      }
    } else if (this.field.required) {
      validations.option = {
        required: requiredIf(() => this.exists())
      }
      validations.otherOption = {}
    } else {
      validations.option = {}
      validations.otherOption = {}
    }
    return validations
  }

  parseSubmitValue () {
    if (!this.previousSubmitValue) {
      return this.emptyValue
    }
    if (this.isOtherOptionSubmitValue(this.previousSubmitValue)) {
      return {
        showOtherOption: this.field.allowOther,
        option: this.optionEmptyValue,
        otherOption: this.field.allowOther ? this.parseOtherOptionSubmitValue(this.previousSubmitValue) : this.otherOptionEmptyValue
      }
    } else {
      return {
        showOtherOption: false,
        option: this.previousSubmitValue,
        otherOption: this.otherOptionEmptyValue
      }
    }
  }

  get emptyValue () {
    return {
      showOtherOption: false,
      option: this.optionEmptyValue,
      otherOption: this.otherOptionEmptyValue
    }
  }

  isEmpty () {
    return this.value === undefined || (this.field.allowOther && this.isOtherOptionSubmitValue(this.value)
      ? isEqual(this.parseOtherOptionSubmitValue(this.value), this.otherOptionEmptyValue)
      : isEqual(this.value, this.optionEmptyValue)
    )
  }

  get optionEmptyValue () {
    return this.field.multiple ? [] : undefined
  }

  get otherOptionEmptyValue () {
    return ''
  }

  getOtherOptionSubmitValue (inputText) {
    if (inputText === this.otherOptionEmptyValue) {
      return this.field.multiple ? [] : this.optionEmptyValue
    }
    const otherOption = {
      value: inputText,
      label: LocalizedString('dynamic-form.fields.select.allow-other.submit-value', { input: inputText })
    }

    return this.field.multiple ? [otherOption] : otherOption
  }

  parseOtherOptionSubmitValue (submitValue) {
    return this.field.multiple ? (submitValue ?? [])[0]?.value : submitValue?.value
  }

  isOtherOptionSubmitValue (submitValue) {
    const optionValue = this.parseOtherOptionSubmitValue(submitValue)
    return isEqual(submitValue, this.getOtherOptionSubmitValue(optionValue))
  }

  get value () {
    return this.field.allowOther && this.$v.$model.showOtherOption
      ? this.getOtherOptionSubmitValue(this.$v.$model.otherOption)
      : this.$v.$model.option
  }

  get simpleValue () {
    return this.value?.value
  }

  exists () {
    if ('optionsLimitedBy' in this.field) {
      const limiterField = this.findField({ name: this.field.optionsLimitedBy.field })
      if (limiterField?.value?.value === undefined) {
        return false
      }
    }
    return super.exists()
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'SelectField',
  components: {
    SelectWithOtherWrapper,
    CheckboxGroup: () => import('@/components/utils/fields/CheckboxGroup.vue'),
    RadioGroup: () => import('@/components/utils/fields/RadioGroup.vue'),
    SelectInput: () => import('@/components/utils/fields/Select.vue'),
    SelectWithSearch: () => import('@/components/utils/fields/SelectWithSearch.vue')
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
    const field = this.formField.field
    const initialOptions = field.optionsLimitedBy === undefined ? field.options : []
    if (!field.collapse || field.options.length <= MAX_NON_SEARCHABLE_LENGTH) {
      // it will never use select with search
      return {
        limitedOptions: initialOptions
      }
    }

    const { SelectWithSearchHelper } = require('@/components/utils/fields/SelectWithSearch.vue')
    class FieldOptionsHelper extends SelectWithSearchHelper {
      constructor (vm, options, selectedValue) {
        super(options)
        this.vm = vm
        this.selected = selectedValue
      }

      getOptionFromValue (value) {
        return value
      }

      getOptionValue (option) {
        return option
      }

      getOptionId (option) {
        return String(option.value)
      }

      getOptionText (option) {
        return this.vm.translate(option.label)
      }
    }
    return {
      searchHelper: new FieldOptionsHelper(this, initialOptions, this.formField.$v.option.$model),
      searchInput: '',
      limitedOptions: initialOptions
    }
  },
  computed: {
    field () {
      return this.formField.field
    },
    $v () {
      return this.formField.$v
    },
    maxNonSearchableLength () {
      return MAX_NON_SEARCHABLE_LENGTH
    },
    optionsLimiter () {
      if (this.field.optionsLimitedBy?.field) {
        return this.formField.findField({ name: this.field.optionsLimitedBy.field })
      }
      return undefined
    }
  },
  watch: {
    '$v.$model.showOtherOption' (value) {
      if (!value) {
        this.$nextTick(() => {
          const field = this.$refs.field
          const input = field.$refs.input
          input.focus()
        })
      } else {
        if (this.searchHelper) {
          this.searchHelper.selected = this.formField.optionEmptyValue
          this.searchInput = ''
        }
        this.$v.option.$model = this.formField.optionEmptyValue
      }
    },
    'optionsLimiter.value': {
      immediate: true,
      deep: true,
      handler (limiterOption) {
        if (!this.formField.exists()) {
          this.$v.option.$model = this.formField.optionEmptyValue
          this.$v.otherOption.$model = this.formField.otherOptionEmptyValue
          if (this.searchHelper) {
            this.searchHelper.selected = this.formField.optionEmptyValue
            this.searchInput = ''
          }
          this.$v.$reset()
          return
        }
        if (!this.optionsLimiter) {
          this.limitedOptions = this.field.options
          if (this.searchHelper) {
            this.searchHelper.options = this.limitedOptions
          }
          return
        }
        const dirty = this.$v.$anyDirty
        const limiter = limiterOption?.value
        if (limiter === undefined) {
          this.limitedOptions = []
        } else {
          this.limitedOptions = this.field.options.filter(option => isEqual(option.optionFor, limiter))
        }

        const selected = this.$v.option.$model
        const shouldResetSelected = !isEqual(selected, this.formField.optionEmptyValue) &&
          (this.field.multiple
            // every selected option must be present in the array of limited options - if not, we should reset
            ? !this.selected.every(selectedOption => this.limitedOptions.find(option => isEqual(selectedOption, option)) !== undefined)
            // the selected option must be present in the array of limited options - if not, we should reset
            : !this.limitedOptions.find(option => isEqual(option, selected))
          )
        if (shouldResetSelected) {
          this.$v.option.$model = this.formField.optionEmptyValue
          if (this.searchHelper) {
            this.searchHelper.selected = this.formField.optionEmptyValue
            this.searchInput = ''
          }
        }
        if (this.searchHelper) {
          // update search helper options
          this.searchHelper.options = this.limitedOptions
        }
        if (!dirty) {
          this.$v.$reset()
        }
      }
    }
  }
}
</script>
