<template>
  <date-picker
    v-if="field.date && !field.time"
    v-model="$v.date.$model"
    :name="$attrs.name || field.field"
    :required="field.required"
    :invalid="$v.date.$error"
    :label="translate(field.label)"
    :description="translate(field.description)"
    variant="outlined"
  >
    <template
      v-if="field.tooltip"
      #tooltip
    >
      <p>{{ translate(field.tooltip) }}</p>
    </template>
    <template #error-message>
      <template v-if="$v.date.$params.required && !$v.date.required">
        {{ $t('dynamic-form.fields.datetime.errors.required') }}
      </template>
      <template v-else-if="!$v.date.isValid">
        {{ $t('dynamic-form.fields.datetime.errors.date') }}
      </template>
    </template>
  </date-picker>
  <time-input
    v-else-if="field.time && !field.date"
    v-model="$v.time.$model"
    :name="`${$attrs.name || field.field}-time`"
    :required="field.required"
    :invalid="$v.time.$error"
    :label="translate(field.label)"
    :description="translate(field.description)"
    variant="outlined"
    :step="field.step"
  >
    <template #error-message>
      <template v-if="$v.time.$params.required && !$v.time.required">
        {{ $t('dynamic-form.fields.datetime.errors.required') }}
      </template>
      <template v-else-if="!$v.time.isValid">
        {{ $t('dynamic-form.fields.datetime.errors.time') }}
      </template>
    </template>
  </time-input>
  <div
    v-else
    class="f-field"
    :class="{ 'f-field--required': field.required }"
  >
    <fieldset
      :aria-labelledby="`datetime-${$attrs.name || field.field}-label`"
      :aria-describedby="field.description ? `datetime-${$attrs.name || field.field}-description` : undefined"
      :aria-invalid="String($v.$anyError)"
      :aria-required="String(field.required)"
    >
      <legend
        :id="`datetime-${$attrs.name || field.field}-label`"
        class="f-field__label"
      >
        {{ translate(field.label) }}
      </legend>
      <p
        v-if="field.description"
        :id="`datetime-${$attrs.name || field.field}-description`"
        class="f-field__description"
      >
        {{ translate(field.description) }}
      </p>
      <div class="f-inline-group">
        <date-picker
          v-model="$v.date.$model"
          :name="`${$attrs.name || field.field}-date`"
          :required="field.required"
          :invalid="$v.date.$error"
          :label="$t('datetime.aria.date')"
          variant="outlined"
          class="f-field--hidden-label"
        >
          <template #error-message>
            <template v-if="$v.date.$params.required && !$v.date.required">
              {{ $t('dynamic-form.fields.datetime.errors.required') }}
            </template>
            <template v-else-if="!$v.date.isValid">
              {{ $t('dynamic-form.fields.datetime.errors.date') }}
            </template>
          </template>
        </date-picker>
        <time-input
          v-model="$v.time.$model"
          :name="`${$attrs.name || field.field}-time`"
          :required="field.required"
          :invalid="$v.time.$error"
          :label="$t('datetime.aria.time')"
          variant="outlined"
          :step="field.step"
          class="f-field--hidden-label"
        >
          <template #error-message>
            <template v-if="$v.time.$params.required && !$v.time.required">
              {{ $t('dynamic-form.fields.datetime.errors.required') }}
            </template>
            <template v-else-if="!$v.time.isValid">
              {{ $t('dynamic-form.fields.datetime.errors.time') }}
            </template>
          </template>
        </time-input>
      </div>
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
    </fieldset>
  </div>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { DATE_PICKER_FORMAT } from '@/components/utils/fields/DatePicker.vue'
import { TIME_INPUT_FORMAT } from '@/components/utils/fields/TimeInput.vue'
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import { withParams, req } from 'vuelidate/lib/validators/common'
import dayjs from 'dayjs'
import CustomParseFormat from 'dayjs/plugin/customParseFormat'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

dayjs.extend(CustomParseFormat)

const ISO_DATE_FORMAT = 'YYYY-MM-DD'
const ISO_TIME_FORMAT = 'THH:mm:ss.SSSZ'
const ISO_DATETIME_FORMAT = ISO_DATE_FORMAT + ISO_TIME_FORMAT

export const isValidDate = withParams(
  { type: 'date' },
  value => {
    return !req(value) || (value !== undefined && dayjs(value, DATE_PICKER_FORMAT, true).isValid())
  }
)

export const isValidTime = withParams(
  { type: 'time' },
  value => {
    return !req(value) || (value !== undefined && dayjs(value, TIME_INPUT_FORMAT, true).isValid())
  }
)

export const isoDateTime = withParams(
  { type: 'datetime' },
  value => {
    return !req(value) || (value !== undefined && dayjs(value, ISO_DATETIME_FORMAT).isValid())
  }
)

const IsoDateTimeFormat = {
  parse (value) {
    if (value === undefined) {
      return { date: undefined, time: undefined }
    }
    const [date, time] = value.split('T')
    const d = dayjs(date ?? NaN, ISO_DATE_FORMAT, false)
    const t = dayjs(`T${time ?? NaN}`, ISO_TIME_FORMAT, false)

    return {
      date: d.isValid() ? d.format(DATE_PICKER_FORMAT) : undefined,
      time: t.isValid() ? t.format(TIME_INPUT_FORMAT) : undefined
    }
  },
  format ({ date, time }) {
    if (date !== undefined && time !== undefined) {
      const dt = dayjs(`${date} ${time}`, `${DATE_PICKER_FORMAT} ${TIME_INPUT_FORMAT}`, true) // true for strict parsing
      if (dt.isValid()) {
        return dt.toISOString()
      }
    } else if (date !== undefined) {
      const d = dayjs(date, DATE_PICKER_FORMAT, true)
      if (d.isValid()) {
        return d.format(ISO_DATE_FORMAT)
      }
    } else if (time !== undefined) {
      const t = dayjs(time, TIME_INPUT_FORMAT, true)
      if (t.isValid()) {
        return t.format(ISO_TIME_FORMAT)
      }
    }
    return undefined
  }
}

export const extend = superclass => class extends superclass {
  constructor () {
    super(...arguments)

    // modify field to avoid having a nonsense field
    // if they're both falsy, then it's just a date
    if (this.field.time === false && (this.field.date === undefined || this.field.date === false)) {
      this.field.date = true
    }
  }

  get validations () {
    const validations = { }
    if (this.field.time) {
      validations.time = { isValid: isValidTime }
    }
    if (this.field.date) {
      validations.date = { isValid: isValidDate }
    }
    if (this.field.required) {
      if (validations.time) validations.time.required = requiredIf(() => this.exists())
      if (validations.date) validations.date.required = requiredIf(() => this.exists())
    }
    return validations
  }

  get value () {
    return IsoDateTimeFormat.format({ date: this.$v.$model?.date, time: this.$v.$model?.time })
  }

  parseSubmitValue () {
    if (this.previousSubmitValue) {
      const { date, time } = IsoDateTimeFormat.parse(this.previousSubmitValue)
      if (!!date === this.field.date && !!time === this.field.time) {
        return { date, time }
      }
    }
    return this.emptyValue
  }

  get emptyValue () {
    return { date: undefined, time: undefined }
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'DateTimeField',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue'),
    DatePicker: () => import('@/components/utils/fields/DatePicker.vue'),
    TimeInput: () => import('@/components/utils/fields/TimeInput.vue')
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
    field () {
      return this.formField.field
    },
    $v () {
      return this.formField.$v
    }
  },
  i18n: {
    messages: {
      pt: {
        datetime: {
          aria: {
            date: 'Data',
            time: 'Hora'
          }
        }
      },
      en: {
        datetime: {
          aria: {
            date: 'Date',
            time: 'Time'
          }
        }
      }
    }
  }
}
</script>
