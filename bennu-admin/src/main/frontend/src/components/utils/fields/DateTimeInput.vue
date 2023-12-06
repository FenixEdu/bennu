<template>
  <div
    class="f-field"
    :class="{ 'f-field--required': required }"
  >
    <fieldset
      :aria-labelledby="`datetime-${name}-label`"
      :aria-describedby="description ? `datetime-${name}-description` : undefined"
      :aria-invalid="String(invalid)"
      :aria-required="String(required)"
    >
      <legend
        v-if="label"
        :id="`datetime-${name}-label`"
        class="f-field__label"
      >
        {{ label }}
      </legend>
      <p
        v-if="description"
        :id="`datetime-${name}-description`"
        class="f-field__description"
      >
        {{ description }}
      </p>
      <div class="f-inline-group">
        <date-picker
          v-model="date"
          :name="`datetime-${name}-date`"
          :required="required"
          :invalid="invalid || invalidDate"
          :label="$t('datetime.aria.date')"
          :variant="variant"
          :input-format="dateInputFormat"
          :output-format="dateOutputFormat"
          :autofocus="autofocus"
          class="f-field--hidden-label"
        >
          <slot
            slot="error-message"
            name="date-error-message"
          />
        </date-picker>
        <time-input
          v-model="time"
          :name="`datetime-${name}-time`"
          :required="required"
          :invalid="invalid || invalidTime"
          :label="$t('datetime.aria.time')"
          :variant="variant"
          :step="timeStep"
          class="f-field--hidden-label"
        >
          <slot
            slot="error-message"
            name="time-error-message"
          />
        </time-input>
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
          :id="`datetime-${name}-error`"
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
import DatePicker, { DATE_PICKER_FORMAT } from '@/components/utils/fields/DatePicker.vue'
import TimeInput, { TIME_INPUT_FORMAT } from '@/components/utils/fields/TimeInput.vue'
import dayjs from 'dayjs'
import CustomParseFormat from 'dayjs/plugin/customParseFormat'

dayjs.extend(CustomParseFormat)

export default {
  components: {
    DatePicker,
    TimeInput,
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
    dateInputFormat: {
      type: String,
      default: undefined
    },
    dateOutputFormat: {
      type: String,
      default: undefined
    },
    timeStep: {
      type: Number,
      default: undefined
    },
    variant: {
      type: String,
      default: 'underlined',
      validator: (value) => {
        return ['underlined', 'outlined'].includes(value)
      }
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
    invalidDate: {
      type: Boolean,
      default: false
    },
    invalidTime: {
      type: Boolean,
      default: false
    },
    autofocus: {
      type: Boolean,
      default: false
    }
  },
  data () {
    const datetime = this.value !== undefined ? dayjs(this.value) : /* invalid on purpose */ dayjs(null)
    return {
      date: datetime.isValid() ? datetime.format(DATE_PICKER_FORMAT) : undefined,
      time: datetime.isValid() ? datetime.format(TIME_INPUT_FORMAT) : undefined
    }
  },
  i18n: {
    messages: {
      pt: {
        'tooltip-trigger': {
          'aria-label': 'Mostrar dica de preenchimento'
        },
        datetime: {
          aria: {
            date: 'Data',
            time: 'Hora'
          }
        }
      },
      en: {
        'tooltip-trigger': {
          'aria-label': 'Show filling hint'
        },
        datetime: {
          aria: {
            date: 'Date',
            time: 'Time'
          }
        }
      }
    }
  },
  computed: {
    datetime () {
      const datetime = dayjs(`${this.date} ${this.time}`, `${DATE_PICKER_FORMAT} ${TIME_INPUT_FORMAT}`, true)
      return datetime.isValid() ? datetime.toISOString() : undefined
    }
  },
  watch: {
    date: {
      immediate: true,
      handler (newValue) {
        const date = dayjs(newValue ?? null, DATE_PICKER_FORMAT, true)
        this.$emit('update:date', date.isValid() ? date.toISOString().split('T')[0] : newValue)
      }
    },
    time: {
      immediate: true,
      handler (newValue) {
        const time = dayjs(newValue ?? null, TIME_INPUT_FORMAT, true)
        this.$emit('update:time', time.isValid() ? time.toISOString().split('T')[1] : newValue)
      }
    },
    datetime (newValue) {
      this.$emit('input', newValue)
    }
  }
}
</script>
