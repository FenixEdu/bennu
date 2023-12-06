<template>
  <div
    class="f-field f-field--searchable-select"
    :class="{ 'f-field--danger': showAsInvalid, 'f-field--required': required, 'f-field--hint-bar': variant === 'hint-bar', 'f-field--underline': variant === 'underlined', 'f-field--disabled': disabled }"
  >
    <label
      v-if="label"
      :for="id || name"
      class="f-field__label"
    >
      {{ label }}
    </label>
    <p
      v-if="description"
      :id="`combobox-${name}-description`"
      class="f-field__description"
    >
      {{ description }}
    </p>
    <div>
      <div class="f-field__container">
        <input
          :id="id || name"
          ref="input"
          :value="filter"
          autocomplete="off"
          type="text"
          role="combobox"
          :aria-autocomplete="autocomplete ? 'both' : 'list'"
          :aria-expanded="String(listboxExpanded)"
          :aria-activedescendant="selected && listboxExpanded ? `combobox-${name}-option-${searchHelper.getOptionId(selected)}` : undefined"
          :aria-controls="`combobox-${name}-listbox`"
          :aria-describedby="[description ? `combobox-${name}-description` : undefined, variant === 'hint-bar' ? `combobox-${name}-hint` : undefined].concat($attrs['aria-describedby']).join(' ')"
          :aria-required="String(required)"
          :aria-invalid="String(invalid)"
          :aria-errormessage="showAsInvalid ? `combobox-${name}-error` : undefined"
          :aria-disabled="String(disabled)"
          :disabled="disabled"
          class="f-field__input"
          v-bind="$attrs"
          :placeholder="placeholder"
          @blur="onComboboxBlur"
          @focus="onComboboxFocus"
          @keydown.alt.down.prevent.stop="openListbox"
          @keydown.esc.prevent="onComboboxEsc"
          @keydown.down.exact.prevent.stop="selectNextOption"
          @keydown.up.exact.prevent.stop="selectPreviousOption"
          @keydown.enter.prevent.stop="onComboboxEnter"
          @input="onTextChanged($event.target.value)"
        >
        <button
          v-show="variant !== 'hint-bar'"
          :id="`combobox-${name}-btn`"
          :aria-label="label"
          :aria-expanded="String(listboxExpanded)"
          :aria-controls="`combobox-${name}-listbox`"
          :aria-disabled="String(disabled)"
          :disabled="disabled"
          tabindex="-1"
          class="f-field__search"
          type="button"
          @click="openListbox()"
        />
        <div
          v-if="variant === 'hint-bar'"
          class="f-field__hint-bar"
        >
          <slot name="hint-bar">
            <span :id="`combobox-${name}-hint`">
              <slot name="hint-bar-text">
                {{ $t('hint-bar-text') }}
              </slot>
            </span>
            <slot name="hint-bar-svg">
              <svg
                width="25"
                height="18"
                xmlns="http://www.w3.org/2000/svg"
              >
                <g
                  fill="none"
                  fill-rule="evenodd"
                >
                  <rect
                    stroke="currentColor"
                    x=".5"
                    y=".5"
                    width="24"
                    height="17"
                    rx="2"
                  />
                  <g
                    stroke="currentColor"
                    stroke-linecap="round"
                    stroke-width="1.2"
                  >
                    <path
                      stroke-linejoin="round"
                      d="M8 10.5h9v-4h-3"
                    />
                    <path d="M10 12.5l-2-2 2-2" />
                  </g>
                </g>
              </svg>
            </slot>
          </slot>
        </div>
      </div>
      <ul
        :id="`combobox-${name}-listbox`"
        role="listbox"
        :aria-label="label"
        class="f-field__options"
        :class="{ 'f-field__options--hidden': !listboxExpanded }"
      >
        <li
          v-for="(option, index) in filteredOptions"
          :id="`combobox-${name}-option-${searchHelper.getOptionId(option)}`"
          ref="options"
          :key="index"
          :aria-selected="getOptionAriaSelected(option)"
          role="option"
          class="f-field__option"
          @mousedown="isSelectingListboxOption = true"
          @mouseup="closeListbox"
          @click.capture="onListboxOptionClick(index)"
        >
          <slot
            name="option"
            :option="option"
            :index="index"
          />
        </li>
      </ul>
      <transition
        name="validation-fade"
        mode="out-in"
      >
        <p
          v-if="isLoading"
          key="loading"
          aria-live="polite"
          class="f-field__validation f-field__status"
          role="status"
        >
          <slot name="loading-options-text">
            {{ $t('loading') }}
          </slot>
        </p>
        <p
          v-else-if="listboxExpanded && filter !== ''"
          key="count"
          aria-live="polite"
          class="f-field__validation f-field__status"
          :class="{ 'sr-only': filteredOptions.length > 0 }"
          role="status"
        >
          <!-- The count only appears when the listbox is not expanded otherwise its under it -->
          <slot
            name="results-count-text"
            :count="filteredOptions.length"
          >
            {{ $tc('filtered-options-count', filteredOptions.length) }}
          </slot>
        </p>
        <div
          v-else-if="showAsInvalid"
          :id="`combobox-${name}-error`"
          key="error"
          class="f-field__validation"
          role="alert"
        >
          <slot name="error-message" />
        </div>
      </transition>
    </div>
    <div
      v-if="$slots && $slots.tooltip"
      class="f-field__tooltip"
    >
      <tooltip>
        <slot
          slot="message"
          name="tooltip"
        />
        <template #trigger="{ attrs, events }">
          <slot
            name="tooltip-trigger"
            :attrs="attrs"
            :events="events"
          >
            <button
              v-bind="attrs"
              :aria-label="$t('tooltip-trigger.aria-label')"
              v-on="events"
            >
              i
            </button>
          </slot>
        </template>
      </tooltip>
    </div>
  </div>
</template>

<script>
import debounce from 'underscore/modules/debounce'
import isEqual from 'underscore/modules/isEqual'
import { cancellable, isCancellationError } from '@/utils/cancellable-function'

export class SelectWithSearchHelper {
  constructor (options, selected) {
    this.options = options
    this.selected = selected
  }

  /**
   * Returns the value that will be used in v-model
   */
  getOptionValue (option) {
    return option
  }

  /**
   * Returns the text that will be matched against the user input
   */
  getOptionText (option) {
    return option
  }

  /**
   * Returns the option that corresponds to the v-model value
   */
  getOptionFromValue (value) {
    return this.options?.find(option => isEqual(this.getOptionValue(option), value))
  }

  /**
   * Returns a unique string value that identifies the option
   */
  getOptionId (option) {
    return String(option)
  }

  normalize (str) {
    // https://en.wikipedia.org/wiki/Combining_Diacritical_Marks
    const diacriticsRegex = /[\u0300-\u036f]/g
    return str.toLowerCase().normalize('NFD').replace(diacriticsRegex, '')
  }

  filterOptions (query) {
    const filter = this.normalize(query)
    const parseOption = (option) => this.normalize(this.getOptionText(option))
    return this.options.filter(option => parseOption(option).startsWith(filter) || parseOption(option).includes(filter))
  }

  autocomplete (isInsert, query, filteredOptions) {
    const inputText = this.normalize(query)
    const perfectMatch = filteredOptions.findIndex(option => this.normalize(this.getOptionText(option)) === inputText)
    if (perfectMatch >= 0) {
      return {
        selectedIndex: perfectMatch,
        selectionStart: inputText.length,
        selectionEnd: inputText.length
      }
    }
    if (!isInsert) {
      return null
    }
    const prefixMatch = filteredOptions.findIndex(option => this.normalize(this.getOptionText(option)).startsWith(inputText))
    if (prefixMatch >= 0) {
      const selectedOption = filteredOptions[prefixMatch]
      const optionText = this.normalize(this.getOptionText(selectedOption))
      const selectionStart = optionText.indexOf(inputText) + inputText.length
      const selectionEnd = optionText.length
      return {
        selectedIndex: prefixMatch,
        selectionStart,
        selectionEnd
      }
    }
    return null
  }
}

const sleep = (ms, value) => new Promise(resolve => {
  setTimeout(resolve, ms, value)
})

export default {
  name: 'SelectWithSearch',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue')
  },
  inheritAttrs: false,
  i18n: {
    messages: {
      pt: {
        'filtered-options-count': 'Nenhuma opção disponível para o filtro aplicado | Uma opção disponível | {count} opções disponíveis',
        loading: 'A carregar opções…',
        'hint-bar-text': 'Pressione enter para escolher uma opção',
        'tooltip-trigger': {
          'aria-label': 'Mostrar dica de preenchimento'
        }
      },
      en: {
        'filtered-options-count': 'None of the options available match the text | One option available | {count} options available',
        loading: 'Loading options…',
        'hint-bar-text': 'Press enter to choose an option',
        'tooltip-trigger': {
          'aria-label': 'Show filling hint'
        }
      }
    }
  },
  props: {
    value: {
      // we don't want to validate
      validator: value => true,
      default: undefined
    },
    id: {
      type: String,
      default: undefined
    },
    name: {
      type: String,
      required: true
    },
    label: {
      type: String,
      default: undefined
    },
    description: {
      type: String,
      default: undefined
    },
    placeholder: {
      type: String,
      default: undefined
    },
    variant: {
      type: String,
      default: 'underlined',
      validator: (value) => {
        return ['underlined', 'outlined', 'hint-bar'].includes(value)
      }
    },
    invalid: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    required: {
      type: Boolean,
      default: false
    },
    searchHelper: {
      type: SelectWithSearchHelper,
      default: () => new SelectWithSearchHelper()
    },
    autocomplete: {
      type: Boolean,
      default: true
    },
    inputText: {
      type: String,
      default: ''
    },
    autofocus: {
      type: Boolean,
      default: false
    }
  },
  data () {
    const selectedOption = this.searchHelper.getOptionFromValue(this.value)
    return {
      filter: selectedOption !== undefined ? this.searchHelper.getOptionText(selectedOption) : this.inputText,
      filteredOptions: [],
      listboxExpanded: false,
      previousFilter: '',
      isLoading: false,
      isSelectingListboxOption: false
    }
  },
  computed: {
    selected: {
      get () {
        return this.searchHelper.selected
      },
      set (option) {
        // FIXME:
        // eslint-disable-next-line vue/no-mutating-props
        this.searchHelper.selected = option
        if (this.autocomplete) {
          this.$emit('input', option && this.searchHelper.getOptionValue(option))
        }
      }
    },
    showAsInvalid () {
      return this.invalid && !this.listboxExpanded
    }
  },
  watch: {
    '$i18n.locale' (newValue, oldValue) {
      if (this.selected && this.autocomplete) {
        this.filter = this.searchHelper.getOptionText(this.selected)
      }
    },
    'searchHelper.selected' (newValue) {
      if (newValue !== undefined && this.autocomplete) {
        this.filter = this.searchHelper.getOptionText(newValue)
      }
      this.$emit('update:activeSuggestion', newValue)
    },
    filter: {
      immediate: true,
      handler (newValue, oldValue) {
        if (newValue !== oldValue) {
          this.$emit('update:inputText', newValue)
        }
      }
    },
    inputText (newValue, oldValue) {
      if (newValue !== oldValue) {
        this.filter = newValue
      }
    }
  },
  mounted () {
    if (this.autofocus) {
      this.$nextTick(() => {
        this.$refs.input.focus()
      })
    }
  },
  methods: {
    filterOptions: cancellable(async function (filter) {
      // this function has side effects but it's okay because we want multiple
      // calls to this function to set isLoading to true anyway
      const filterPromise = this.searchHelper.filterOptions(filter)
      const loadingToken = Symbol('loading')
      const loadingPromise = sleep(300, loadingToken)
      const result = await Promise.race([loadingPromise, filterPromise])
      if (result === loadingToken) {
        this.isLoading = true
      }
      return filterPromise
    }),
    clearFilter () {
      this.filter = ''
    },
    openListbox () {
      this.listboxExpanded = true
    },
    closeListbox () {
      this.listboxExpanded = false
    },
    focusOnCombobox () {
      const comboboxEl = this.$refs.input
      comboboxEl.focus()
    },
    async onComboboxFocus () {
      try {
        this.filteredOptions = await this.filterOptions(this.filter)
        this.openListbox()
      } catch (err) {
        if (!isCancellationError(err)) {
          throw err
        }
      } finally {
        this.isLoading = false
      }
    },
    onComboboxBlur () {
      // TODO: if the combobox is blurred immediately after it is focused
      // before this.filterOptions resolves, the combobox may stay open
      // i couldn't find any clean way to cancel the request :(
      // defining a property "abort" on filterOptions (`this.filterOptions.abort()`)
      // doesn't work because Vue (?) strips out the properties on methods

      if (!this.isSelectingListboxOption) {
        // without setTimeout, the listbox may be closed before pointer events on the options are fired
        setTimeout(this.closeListbox, 100)
      }
    },
    onComboboxEsc (event) {
      if (this.listboxExpanded) {
        event.stopPropagation()
        this.closeListbox()
      } else if (this.filter !== '') {
        event.stopPropagation()
        this.selected = undefined
        this.clearFilter()
      }
    },
    async onComboboxEnter () {
      this.closeListbox()
      const comboboxEl = this.$refs.input
      comboboxEl.setSelectionRange(this.filter.length, this.filter.length)
      if (this.selected) {
        this.$emit('input', this.selected)
      }
    },
    onListboxOptionClick (index) {
      this.selectListboxOption(index)
      this.$emit('input', this.selected)
      this.isSelectingListboxOption = false
      this.closeListbox()
    },
    getOptionAriaSelected (option) {
      return isEqual(this.selected, this.searchHelper.getOptionValue(option)) ? 'true' : undefined
    },
    async selectNextOption () {
      if (!this.listboxExpanded) {
        try {
          this.filteredOptions = await this.filterOptions(this.filter)
        } catch (err) {
          if (!isCancellationError(err)) {
            throw err
          }
        } finally {
          this.isLoading = false
        }
      }
      const indexOfSelected = this.filteredOptions.indexOf(this.selected)
      const indexOfNextOption = indexOfSelected + 1
      if (indexOfNextOption < this.filteredOptions.length) {
        this.selectListboxOption(indexOfNextOption)
      } else {
        this.selectListboxOption(0)
      }
    },
    async selectPreviousOption () {
      if (!this.listboxExpanded) {
        try {
          this.filteredOptions = await this.filterOptions(this.filter)
        } catch (err) {
          if (!isCancellationError(err)) {
            throw err
          }
        } finally {
          this.isLoading = false
        }
      }
      let indexOfSelected = this.filteredOptions.indexOf(this.selected)
      if (indexOfSelected === -1) {
        indexOfSelected = this.filteredOptions.length
      }
      const indexOfPreviousOption = indexOfSelected - 1
      if (indexOfPreviousOption > -1) {
        this.selectListboxOption(indexOfPreviousOption)
      } else {
        this.selectListboxOption(this.filteredOptions.length - 1)
      }
    },
    async selectListboxOption (index) {
      this.openListbox()
      const option = this.filteredOptions[index]
      this.selected = option
      if (option === undefined) {
        return
      }
      if (this.autocomplete) {
        this.filter = this.searchHelper.getOptionText(option)
      }
      await this.$nextTick()
      const optionEl = this.$refs.options[index]
      if (optionEl) {
        optionEl.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
      }
    },
    onTextChanged: debounce(async function (text) {
      const inputText = text
      this.filter = inputText
      const isInsert = inputText.length > this.previousFilter.length
      try {
        this.focusOnCombobox()
        this.closeListbox()
        const filteredOptions = await this.filterOptions(inputText)
        this.openListbox()
        this.isLoading = false
        this.filteredOptions = filteredOptions
        this.previousFilter = inputText

        if (this.autocomplete) {
          const autocompleteResult = this.searchHelper.autocomplete(isInsert, inputText, filteredOptions)
          if (autocompleteResult !== null) {
            const { selectionStart, selectionEnd, selectedIndex } = autocompleteResult
            await this.selectListboxOption(selectedIndex)
            const comboboxEl = this.$refs.input
            comboboxEl.setSelectionRange(selectionStart, selectionEnd)
          } else {
            this.selected = undefined
          }
        } else {
          this.selectListboxOption(0)
        }
      } catch (err) {
        if (!isCancellationError(err)) {
          this.isLoading = false
          throw err
        }
      }
    }, 500)
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/variables";

.f-field__options--hidden {
  display: none;
}

.f-field__status {
  color: $gray;
}
.f-field__input:focus ~ .f-field__status {
  opacity: 1;
}
</style>
