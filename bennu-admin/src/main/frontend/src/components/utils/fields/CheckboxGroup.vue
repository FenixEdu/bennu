<template>
  <div
    class="f-field f-field--checkbox-group"
    :class="{ 'f-field--danger': invalid, 'f-field--inline': inline, 'f-field--required': required, 'f-field--disabled': disabled }"
  >
    <fieldset
      ref="input"
      :aria-labelledby="label ? `checkbox-group-${name}-label` : undefined"
      :aria-describedby="[description ? `checkbox-group-${name}-description` : undefined].concat($attrs['aria-describedby']).join(' ')"
      :aria-invalid="String(invalid)"
      :aria-errormessage="invalid ? `checkbox-group-${name}-error` : undefined"
      aria-multiselectable="true"
      :aria-orientation="inline ? 'horizontal' : 'vertical'"
      :aria-required="String(required)"
      :aria-disabled="String(disabled)"
      v-bind="$attrs"
    >
      <legend
        v-if="label"
        :id="`checkbox-group-${name}-label`"
        class="f-field__label"
      >
        {{ label }}
      </legend>
      <p
        v-if="description"
        :id="`checkbox-group-${name}-description`"
        class="f-field__description"
      >
        {{ description }}
      </p>
      <div class="f-field__options">
        <div
          v-for="(option, index) in options"
          :key="index"
          class="f-field--checkbox"
          role="option"
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
          :id="`checkbox-group-${name}-error`"
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
  name: 'CheckboxGroup',
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
