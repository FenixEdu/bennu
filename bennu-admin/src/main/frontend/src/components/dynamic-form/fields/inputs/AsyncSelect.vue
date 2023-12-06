<template>
  <select-with-other-wrapper
    :form-field="formField"
    v-bind="$attrs"
  >
    <template #default="{ attrs }">
      <select-with-search
        ref="select-with-search"
        v-model="$v.option.$model"
        :required="!$v.$model.showOtherOption && field.required"
        :name="$attrs.name || field.field"
        :label="translate(field.label)"
        :description="translate(field.description)"
        :search-helper="searchHelper"
        :invalid="$v.option.$error"
        variant="outlined"
        :input-text.sync="searchInput"
        v-bind="attrs"
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
            {{ $t('dynamic-form.fields.async-select.errors.required') }}
          </template>
        </template>
      </select-with-search>
    </template>
  </select-with-other-wrapper>
</template>

<script>
import axios from 'axios'
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import isEqual from 'underscore/modules/isEqual'

import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'
import { LocalizedString } from '@/i18n'
import SelectWithSearch, { SelectWithSearchHelper } from '@/components/utils/fields/SelectWithSearch.vue'
import SelectWithOtherWrapper from '@/components/dynamic-form/fields/inputs/helpers/AllowOtherWrapper.vue'

class AsyncSelectHelper extends SelectWithSearchHelper {
  constructor (vm, formField, selectedValue) {
    super(null, selectedValue)
    this.formField = formField
    this.vm = vm
    this.field = formField.field
    this.extraOptionsProviderArgs = formField.extraOptionsProviderArgs
  }

  async filterOptions (query) {
    const siblingValues = Object.fromEntries(
      this.formField.getSiblingFields()
        .filter(f => f.exists())
        .map(f => [f.field.field, f.simpleValue])
    )
    const response = await axios.get(`${this.field.optionsProvider}`, {
      params: {
        query,
        ...(this.vm.optionsLimiter && { optionsLimitedBy: this.vm.optionsLimiter.value?.value ?? '' }),
        ...this.extraOptionsProviderArgs,
        ...siblingValues
      }
    })
    return response.data
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

export const extend = superclass => class extends superclass {
  constructor ({ field, submitValue, uniqueId, location, form, parent }, options) {
    super({ field, submitValue, uniqueId, location, form, parent }, options)
    this.extraOptionsProviderArgs = {} // can be overriden by specific implementations of the field
  }

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
        otherOption: this.field.allowOther ? this.previousSubmitValue.value : this.otherOptionEmptyValue
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
      ? isEqual(this.value.value, this.otherOptionEmptyValue)
      : isEqual(this.value, this.optionEmptyValue)
    )
  }

  get optionEmptyValue () {
    return undefined
  }

  get otherOptionEmptyValue () {
    return ''
  }

  getOtherOptionSubmitValue (inputText) {
    if (inputText === this.otherOptionEmptyValue) {
      return this.optionEmptyValue
    }
    return {
      value: inputText,
      label: LocalizedString('dynamic-form.fields.select.allow-other.submit-value', { input: inputText })
    }
  }

  isOtherOptionSubmitValue (submitValue) {
    return isEqual(submitValue, this.getOtherOptionSubmitValue(submitValue?.value))
  }

  get value () {
    return this.field.allowOther && this.$v?.$model.showOtherOption
      ? this.getOtherOptionSubmitValue(this.$v?.$model.otherOption)
      : this.$v?.$model.option
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
  components: {
    SelectWithSearch,
    SelectWithOtherWrapper
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
    return {
      searchHelper: new AsyncSelectHelper(this, this.formField, this.formField.$v.option?.$model),
      searchInput: ''
    }
  },
  computed: {
    field () {
      return this.formField.field
    },
    $v () {
      return this.formField.$v
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
          const ref = this.$refs['select-with-search']
          // FIXME: this will break if SelectWithSearch ever changes the name of the ref
          // but we really need to switch focus back to the input, it would be bad ux otherwise
          const searchInput = ref.$refs.input
          searchInput.focus()
        })
      } else {
        this.searchHelper.selected = this.formField.optionEmptyValue
        this.$v.option.$model = this.formField.optionEmptyValue
        this.searchInput = ''
      }
    },
    'optionsLimiter.value': {
      immediate: true,
      deep: true,
      async handler (newValue) {
        if (!this.formField.exists()) {
          this.$v.option.$model = this.formField.optionEmptyValue
          this.$v.otherOption.$model = this.formField.otherOptionEmptyValue
          this.searchHelper.selected = this.formField.optionEmptyValue
          this.searchInput = ''
          this.$v.$reset()
          return
        }
        const dirty = this.$v.$anyDirty

        const selectedOption = this.$v.option.$model
        const selectedOptionLabel = selectedOption?.label?.[this.parseLocaleTag(this.$i18n.locale)]
        const limitedOptions = this.optionsLimiter && newValue === undefined
          ? []
          : await this.searchHelper.filterOptions(selectedOptionLabel ?? '')
        if (selectedOption !== undefined && !limitedOptions.find(option => isEqual(option, selectedOption))) {
          this.$v.option.$model = this.formField.optionEmptyValue
          this.searchHelper.selected = this.formField.optionEmptyValue
          this.searchInput = ''
        }

        if (!dirty) {
          this.$v.$reset()
        }
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.f-group {
  width: 100%;
}
</style>
