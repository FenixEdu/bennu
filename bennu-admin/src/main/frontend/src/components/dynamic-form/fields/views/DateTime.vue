<template>
  <p>
    <strong>{{ translate(field.label) }}:</strong>
    <template v-if="isEmpty || displayValue === undefined">
      <span class="sr-only">
        {{ $t('dynamic-form.fields.empty-state.aria-label') }}
      </span>
      <span
        aria-hidden="true"
        class="u-text-secondary"
      >
        {{ $t('dynamic-form.fields.empty-state.text') }}
      </span>
    </template>
    <template v-else>
      {{ displayValue }}
    </template>
  </p>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import dayjs from 'dayjs'
import CustomParseFormat from 'dayjs/plugin/customParseFormat'
dayjs.extend(CustomParseFormat)

const ISO_DATE_FORMAT = 'YYYY-MM-DD'
const ISO_TIME_FORMAT = 'THH:mm:ss.SSSZ'
const ISO_DATETIME_FORMAT = ISO_DATE_FORMAT + ISO_TIME_FORMAT

export default {
  name: 'DateTimeField',
  mixins: [
    TranslateApiStringMixin
  ],
  props: {
    snapshot: {
      type: Object,
      required: true
    }
  },
  computed: {
    field () {
      return this.snapshot.field
    },
    value () {
      return this.snapshot.value
    },
    isEmpty () {
      return this.snapshot.isEmpty
    },
    displayValue () {
      let parseFormat
      let displayFormat
      if (this.field.date && this.field.time) {
        parseFormat = ISO_DATETIME_FORMAT
        displayFormat = 'LLL'
      } else if (this.field.time) {
        parseFormat = ISO_TIME_FORMAT
        displayFormat = this.field.step >= 60 ? 'LT' : 'LTS'
      } else if (this.field.date) {
        parseFormat = ISO_DATE_FORMAT
        displayFormat = 'LL'
      }
      if (parseFormat) {
        return dayjs(this.value, parseFormat).locale(this.$i18n.locale).format(displayFormat)
      }
      return undefined
    }
  }
}
</script>
