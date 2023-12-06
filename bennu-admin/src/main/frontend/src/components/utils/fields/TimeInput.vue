<template>
  <div
    :class="{ 'f-field--danger': invalid, 'f-field--underline': variant === 'underlined', 'f-field--required': required, 'f-field--disabled': disabled }"
    class="f-field"
  >
    <input
      :id="name"
      ref="input"
      v-model="input"
      :name="name"
      :placeholder="placeholder"
      :aria-required="String(required)"
      :aria-describedby="[description ? `time-${name}-description` : undefined].concat($attrs['aria-describedby']).join(' ')"
      :aria-invalid="String(invalid)"
      :aria-errormessage="invalid ? `time-${name}-error` : undefined"
      :disabled="disabled"
      :aria-disabled="String(disabled)"
      :step="effectiveStep"
      type="time"
      class="f-field__input"
      v-bind="$attrs"
      @input="sanitizedInput = $event.target.value"
      @focus="onFocus"
    >
    <label
      v-if="label"
      :for="name"
      class="f-field__label"
    >
      {{ label }}
    </label>
    <p
      v-if="description"
      :id="`time-${name}-description`"
      class="f-field__description"
    >
      {{ description }}
    </p>
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
    <transition name="validation-fade">
      <div
        v-if="invalid"
        :id="`time-${name}-error`"
        class="f-field__validation"
        role="alert"
      >
        <slot name="error-message" />
      </div>
    </transition>
  </div>
</template>

<script>
export const TIME_INPUT_FORMAT = 'HH:mm:ss.SSS'

export default {
  name: 'TimeInput',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue')
  },
  props: {
    value: {
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
    required: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    invalid: {
      type: Boolean,
      default: false
    },
    variant: {
      type: String,
      default: 'underlined',
      validator: (value) => {
        return ['underlined', 'outlined'].includes(value)
      }
    },
    step: {
      type: Number,
      default: 60
    },
    autofocus: {
      type: Boolean,
      default: false
    }
  },
  i18n: {
    messages: {
      pt: {
        'tooltip-trigger': {
          'aria-label': 'Mostrar dica de preenchimento'
        }
      },
      en: {
        'tooltip-trigger': {
          'aria-label': 'Show filling hint'
        }
      }
    }
  },
  data () {
    return {
      input: this.value
    }
  },
  computed: {
    effectiveStep () {
      // granularity from hour to milliseconds
      return [3600, 600, 60, 10, 1, 0.1, 0.01, 0.001].find(fraction => this.step >= fraction) ?? 0.001
    },
    placeholder () {
      if (this.effectiveStep === 3600) {
        return '--:00'
      }
      let placeholder = '--:--'
      if (this.effectiveStep < 60) {
        placeholder += ':--'
      }
      if (this.effectiveStep < 1) {
        placeholder += '.---'
      }
      return placeholder
    },
    pattern () {
      switch (this.effectiveStep) {
        case 3600:
          return '--:00:00.000'
        case 600:
          return '--:-0:00.000'
        case 60:
          return '--:--:00.000'
        case 10:
          return '--:--:-0.000'
        case 1:
          return '--:--:--.000'
        case 0.1:
          return '--:--:--.-00'
        case 0.01:
          return '--:--:--.--0'
        case 0.001:
        default:
          return '--:--:--.---'
      }
    },
    sanitizedInput: {
      get () {
        return this.value
      },
      set (val) {
        const sanitized = this.asPattern(val, this.pattern)
        this.$emit('input', sanitized)
        if (this.$refs.input.type !== 'time') {
          // this is custom behaviour for safari on macos bc it sucks <3
          const formatted = this.asPattern(val, this.placeholder)
          this.input = formatted
          this.$nextTick(() => {
            this.$refs.input.setSelectionRange(val.length, formatted.length)
          })
        }
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
    asPattern (val, pattern) {
      return pattern.split('').map((char, index) => {
        const inputChar = !Number.isNaN(Number(val[index])) ? String(val[index]) : '0'
        return char === '-' ? inputChar : char
      }).join('')
    },
    onFocus () {
      if (this.$refs.input.type !== 'time') {
        // this is custom behaviour for safari on macos bc it sucks <3
        setTimeout(() => {
          this.$refs.input.setSelectionRange(0, this.input?.length ?? 0)
        }, 100)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.f-field__input {
  height: 3rem; // for some reason it had 50px without this
}
</style>
