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
      :type="type"
      :aria-required="String(required)"
      :aria-describedby="[description ? `text-${name}-description` : undefined].concat($attrs['aria-describedby']).join(' ')"
      :aria-invalid="String(invalid)"
      :aria-errormessage="invalid ? `text-${name}-error` : undefined"
      :aria-disabled="String(disabled)"
      :disabled="disabled"
      class="f-field__input"
      autocomplete="on"
      autocorrect="off"
      autocapitalize="off"
      spellcheck="false"
      v-bind="$attrs"
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
      :id="`text-${name}-description`"
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
    <div
      v-if="$slots && $slots.length"
      class="f-field__length"
    >
      <slot name="length" />
    </div>
    <transition name="validation-fade">
      <div
        v-if="invalid"
        :id="`text-${name}-error`"
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
  name: 'TextInput',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue')
  },
  props: {
    value: {
      type: String,
      required: true
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
      required: false,
      default: ''
    },
    required: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    type: {
      type: String,
      default: 'text'
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
