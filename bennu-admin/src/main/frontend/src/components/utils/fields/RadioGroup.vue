<template>
  <div
    class="f-field f-field--radiogroup"
    :class="{ 'f-field--danger': invalid, 'f-field--inline': inline, 'f-field--required': required, 'f-field--disabled': disabled }"
  >
    <fieldset
      ref="input"
      role="radiogroup"
      :aria-labelledby="label ? `radiogroup-${name}-label` : undefined"
      :aria-describedby="[description ? `radiogroup-${name}-description` : undefined].concat($attrs['aria-describedby']).join(' ')"
      :aria-invalid="String(invalid)"
      :aria-errormessage="invalid ? `radiogroup-${name}-error` : undefined"
      :aria-orientation="inline ? 'horizontal' : 'vertical'"
      :aria-required="String(required)"
      :aria-disabled="String(disabled)"
      v-bind="$attrs"
    >
      <legend
        v-if="label"
        :id="`radiogroup-${name}-label`"
        class="f-field__label"
      >
        {{ label }}
      </legend>
      <p
        v-if="description"
        :id="`radiogroup-${name}-description`"
        class="f-field__description"
      >
        {{ description }}
      </p>
      <div class="f-field__options">
        <div
          v-for="(option, index) in options"
          :key="index"
          class="f-field--radio"
          :class="`f-field--${size}`"
        >
          <slot
            name="option"
            :option="option"
            :index="index"
            :attrs="{ disabled }"
          />
        </div>
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
      <transition name="validation-fade">
        <div
          v-if="invalid"
          :id="`radiogroup-${name}-error`"
          class="f-field__validation"
          role="alert"
        >
          <slot name="error-message" />
        </div>
      </transition>
    </fieldset>
  </div>
</template>

<script>
export default {
  name: 'RadioGroup',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue')
  },
  props: {
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
    options: {
      type: Array,
      required: true
    },
    invalid: {
      type: Boolean,
      default: false
    },
    inline: {
      type: Boolean,
      default: false
    },
    size: {
      type: String,
      default: 'sm'
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
  }
}
</script>
