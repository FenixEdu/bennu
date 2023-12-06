<template>
  <div
    :class="{ 'f-field--danger': invalid, 'f-field--underline': variant === 'underlined', 'f-field--required': required, 'f-field--disabled': disabled }"
    class="f-field f-field--select"
  >
    <select
      :id="name"
      ref="input"
      v-model="input"
      :aria-required="String(required)"
      :aria-describedby="[description ? `select-${name}-description` : undefined].concat($attrs['aria-describedby']).join(' ')"
      :aria-invalid="String(invalid)"
      :aria-errormessage="invalid ? `select-${name}-error` : undefined"
      :aria-disabled="String(disabled)"
      :disabled="disabled"
      class="f-field__input"
      v-bind="$attrs"
    >
      <option
        v-if="placeholder !== false"
        :value="undefined"
        :selected="input === undefined"
      >
        {{ placeholder || $t('empty') }}
      </option>
      <slot
        name="options"
        :options="options"
      />
    </select>
    <label
      v-if="label"
      :for="name"
      class="f-field__label"
    >
      {{ label }}
    </label>
    <p
      v-if="description"
      :id="`select-${name}-description`"
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
        :id="`select-${name}-error`"
        class="f-field__validation"
        role="alert"
      >
        <slot name="error-message" />
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'SelectInput',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue')
  },
  i18n: {
    messages: {
      pt: {
        empty: 'Escolha uma opção…',
        'tooltip-trigger': {
          'aria-label': 'Mostrar dica de preenchimento'
        }
      },
      en: {
        empty: 'Select an option…',
        'tooltip-trigger': {
          'aria-label': 'Show filling hint'
        }
      }
    }
  },
  props: {
    value: {
      required: true,
      // we don't care about the value
      validator: () => true
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
      type: [String, Boolean],
      default: undefined,
      validator (value) {
        return typeof value !== 'boolean' || value === false
      }
    },
    options: {
      type: Array,
      required: true
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
    }
  },
  computed: {
    input: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('input', val)
      }
    }
  },
  mounted () {
    if (this.autofocus) {
      this.$nextTick(() => {
        this.$refs.input.focus()
      })
    }
  }
}
</script>
