<template>
  <div
    class="f-field f-field--date"
  >
    <div
      class="f-field"
      :class="{ 'f-field--required': required, 'f-field--disabled': disabled, 'f-field--danger': invalid, 'f-field--underline': variant === 'underlined' }"
    >
      <div class="f-field__input f-field__input-wrapper">
        <input
          :id="`datepicker-${name}-input`"
          ref="input"
          v-model="input"
          class="f-field__input"
          type="text"
          :placeholder="displayPlaceholder"
          aria-autocomplete="none"
          :aria-required="String(required)"
          :aria-describedby="[description ? `datepicker-${name}-description` : undefined].concat($attrs['aria-describedby']).join(' ')"
          :aria-invalid="String(invalid)"
          :aria-errormessage="invalid ? `datepicker-${name}-error` : undefined"
          :aria-disabled="String(disabled)"
          :disabled="disabled"
          v-bind="$attrs"
          @input="parseTextInput($event.target.value)"
        >
        <button
          :ref="`datepicker-${name}-button`"
          class="f-field__dialog-trigger"
          :aria-label="value ? $t('aria.dialog-trigger.has-selection', { value }) : $t('aria.dialog-trigger.no-selection')"
          :aria-disabled="String(disabled)"
          :disabled="disabled"
          type="button"
          @click.prevent="openDatePicker"
        />
      </div>
      <label
        v-if="label"
        class="f-field__label"
        :for="`datepicker-${name}-input`"
      >
        {{ label }} <span class="sr-only">({{ $t('format') }})</span>
      </label>
      <p
        v-if="description"
        :id="`datepicker-${name}-description`"
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
          :id="`datepicker-${name}-error`"
          class="f-field__validation"
          role="alert"
        >
          <slot name="error-message" />
        </div>
      </transition>
    </div>
    <div
      :id="`datepicker-${name}-dialog`"
      class="f-field__dialog"
      :class="{ 'f-field__dialog--hidden': !showDatePicker }"
      role="dialog"
      aria-modal="true"
      :aria-labelledby="`datepicker-${name}-month-year`"
    >
      <div class="dialog__header">
        <p
          :id="`datepicker-${name}-month-year`"
          class="sr-only"
          aria-live="polite"
        >
          {{ currentMonth }} {{ currentYear }}
        </p>
        <div class="dialog__year-month-select">
          <button
            class="arrow arrow--previous"
            :aria-label="$t('aria.arrow-previous-year')"
            @click.prevent="previousYear"
          />
          <span
            class="dialog__year-month"
            aria-hidden="true"
          >
            {{ currentYear }}
          </span>
          <button
            class="arrow arrow--next"
            :aria-label="$t('aria.arrow-next-year')"
            @click.prevent="nextYear"
          />
        </div>
        <div class="dialog__year-month-select">
          <button
            class="arrow arrow--previous"
            :aria-label="$t('aria.arrow-previous-month')"
            @click.prevent="previousMonth"
          />
          <span
            class="dialog__year-month"
            aria-hidden="true"
          >
            {{ currentMonth }}
          </span>
          <button
            class="arrow arrow--next"
            :aria-label="$t('aria.arrow-next-month')"
            @click.prevent="nextMonth"
          />
        </div>
        <div
          :id="`datepicker-${name}-calendar`"
          class="dialog__calendar"
          :aria-labelledby="`datepicker-${name}-month-year`"
          :aria-describedby="`datepicker-${name}-keyboard-instructions`"
          role="grid"
        >
          <div role="rowgroup">
            <div
              role="row"
              class="calendar__weekdays"
            >
              <span
                v-for="(weekday, index) of weekdays"
                :key="index"
                class="calendar__weekday"
                role="columnheader"
                :aria-label="weekday"
              >
                {{ weekdaysMin[index] }}
              </span>
            </div>
          </div>
          <div
            :key="`${currentYear}-${currentMonth}`"
            role="rowgroup"
            class="calendar__month"
          >
            <div
              v-for="(week, weekIndex) of calendar"
              :key="weekIndex"
              role="row"
              class="calendar__week"
            >
              <button
                v-for="(date, dayIndex) of week"
                :ref="getCalendarDayRef(weekIndex, dayIndex)"
                :key="dayIndex"
                :class="{
                  'calendar__day--disabled': isAnotherMonth(date),
                  'calendar__day--selected': date.isSame(valueDayjs)
                }"
                role="gridcell"
                class="calendar__day"
                :aria-selected="date.isSame(valueDayjs) ? 'true' : undefined"
                :tabindex="date.isSame(preSelection) ? 0 : -1"
                @click.prevent="selectDay(weekIndex, dayIndex)"
                @keydown.left.exact.prevent="calendarLeft(weekIndex, dayIndex)"
                @keydown.right.exact.prevent="calendarRight(weekIndex, dayIndex)"
                @keydown.up.exact.prevent="calendarUp(weekIndex, dayIndex)"
                @keydown.down.exact.prevent="calendarDown(weekIndex, dayIndex)"
                @keydown.home.exact.prevent="calendarHome(weekIndex, dayIndex)"
                @keydown.end.exact.prevent="calendarEnd(weekIndex, dayIndex)"
                @keydown.page-up.exact.prevent="calendarPageUp(weekIndex, dayIndex)"
                @keydown.page-down.exact.prevent="calendarPageDown(weekIndex, dayIndex)"
                @keydown.shift.page-up.exact.prevent="calendarShiftPageUp(weekIndex, dayIndex)"
                @keydown.shift.page-down.exact.prevent="calendarShiftPageDown(weekIndex, dayIndex)"
              >
                {{ getDayOfMonth(date) }}
              </button>
            </div>
          </div>
          <p
            :id="`datepicker-${name}-keyboard-instructions`"
            class="sr-only"
          >
            {{ $t('aria.keyboard-instructions') }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { createFocusTrap } from 'focus-trap'
import chunk from 'underscore/modules/chunk'

import dayjs from 'dayjs'
import LocaleData from 'dayjs/plugin/localeData'
import IsSameOrBefore from 'dayjs/plugin/isSameOrBefore'
import IsSameOrAfter from 'dayjs/plugin/isSameOrAfter'
import CustomParseFormat from 'dayjs/plugin/customParseFormat'
dayjs.extend(LocaleData)
dayjs.extend(IsSameOrBefore)
dayjs.extend(IsSameOrAfter)
dayjs.extend(CustomParseFormat)

export const DATE_PICKER_FORMAT = 'DD/MM/YYYY'

export default {
  name: 'DatePicker',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue')
  },
  i18n: {
    messages: {
      pt: {
        format: 'DD/MM/AAAA',
        aria: {
          'dialog-trigger': {
            'has-selection': 'Escolher data. A data atual é: {value}',
            'no-selection': 'Escolher data'
          },
          'arrow-previous-year': 'Ano anterior',
          'arrow-next-year': 'Ano seguinte',
          'arrow-previous-month': 'Mês anterior',
          'arrow-next-month': 'Mês seguinte',
          'keyboard-instructions': 'As teclas direcionais permitem navegar pelas datas'
        },
        'tooltip-trigger': {
          'aria-label': 'Mostrar dica de preenchimento'
        }
      },
      en: {
        format: 'DD/MM/YYYY',
        aria: {
          'dialog-trigger': {
            'has-selection': 'Choose date. The current date is: {value}',
            'no-selection': 'Choose date'
          },
          'arrow-previous-year': 'Previous year',
          'arrow-next-year': 'Next year',
          'arrow-previous-month': 'Previous month',
          'arrow-next-month': 'Next month',
          'keyboard-instructions': 'Cursor keys can navigate dates'
        },
        'tooltip-trigger': {
          'aria-label': 'Show filling hint'
        }
      }
    }
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
    placeholder: {
      type: String,
      default: undefined
    },
    variant: {
      type: String,
      default: 'underlined',
      validator: (value) => {
        return ['underlined', 'outlined'].includes(value)
      }
    },
    // TODO: implement ignored days
    // ignore: {
    //   type: Array,
    //   default: function () {
    //     return []
    //   }
    // },
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
    inputFormat: {
      type: String,
      default: DATE_PICKER_FORMAT
    },
    outputFormat: {
      type: String,
      default: DATE_PICKER_FORMAT
    },
    autofocus: {
      type: Boolean,
      default: false
    }
  },
  data () {
    const date = ['', null, undefined].includes(this.value?.trim()) ? /* invalid on purpose */ dayjs(null) : dayjs(this.value, this.outputFormat, true)
    /** @type {dayjs.Dayjs} */
    const pivot = date.isValid() ? date.startOf('day') : dayjs().startOf('day')
    return {
      input: date.isValid() ? date.format(this.inputFormat) : this.value,
      calendar: this.getCalendar(pivot),
      preSelection: pivot,
      showDatePicker: false,
      /** @type {import('focus-trap').FocusTrap} */
      trap: undefined
    }
  },
  computed: {
    valueDayjs () {
      if (!this.value) {
        return undefined
      }
      const date = dayjs(this.value, this.outputFormat)
      return date.isValid() ? date : undefined
    },
    displayPlaceholder () {
      return this.placeholder ?? this.$t('format')
    },
    weekdays () {
      const localeData = dayjs().locale(this.$i18n.locale).localeData()
      const weekstart = localeData.firstDayOfWeek()
      const weekdays = localeData.weekdays()
      const post = weekdays.slice(weekstart)
      const pre = weekdays.slice(0, weekstart)
      return post.concat(pre)
    },
    weekdaysMin () {
      const localeData = dayjs().locale(this.$i18n.locale).localeData()
      const dayjsWeekdays = localeData.weekdaysMin()
      if (dayjsWeekdays.every(day => day.length === 1)) {
        const weekstart = localeData.firstDayOfWeek()
        const post = dayjsWeekdays.slice(weekstart)
        const pre = dayjsWeekdays.slice(0, weekstart)
        return post.concat(pre)
      } else {
        return this.weekdays.map(d => d[0])
      }
    },
    currentYear () {
      return dayjs(this.preSelection).locale(this.$i18n.locale).format('YYYY')
    },
    currentMonth () {
      return dayjs(this.preSelection).locale(this.$i18n.locale).format('MMMM')
    }
  },
  watch: {
    value (newValue) {
      const date = dayjs(newValue, this.outputFormat)
      const fallback = dayjs().startOf('day')
      if (!newValue || !date.isValid()) {
        this.updatePreSelection(fallback, false)
      } else {
        this.updatePreSelection(date, false)
      }
    },
    '$i18n.locale' () {
      this.calendar = this.getCalendar(this.preSelection)
    }
  },
  mounted () {
    this.trap = createFocusTrap(`#datepicker-${this.name}-dialog`, {
      escapeDeactivates: event => {
        event.stopPropagation()
        return true
      },
      clickOutsideDeactivates: true,
      returnFocusOnDeactivate: true,
      initialFocus: () => {
        const [weekIndex, dayIndex] = this.findInCalendar(this.preSelection, this.calendar)
        const ref = this.getCalendarDayRef(weekIndex, dayIndex)
        return this.$refs[ref][0]
      },
      onDeactivate: () => {
        this.showDatePicker = false
        setTimeout(() => {
          // setTimeout because of the dialog transition
          // so that calendar update is not visible
          this.updatePreSelection(this.valueDayjs ?? dayjs().startOf('day'), false)
        }, 500)
      }
    })

    if (this.autofocus) {
      this.$nextTick(() => {
        this.$refs.input.focus()
      })
    }
  },
  beforeDestroy () {
    this.trap.deactivate()
  },
  methods: {
    isAnotherMonth (date) {
      return !date.isSame(this.preSelection, 'month')
    },
    getDayOfMonth (date) {
      return date.date()
    },
    getCalendar (date) {
      const dayjsDate = dayjs(date).locale(this.$i18n.locale).startOf('day')

      const startOfMonth = dayjsDate.startOf('month').startOf('day')
      const endOfMonth = dayjsDate.endOf('month').startOf('day')

      const gridStart = startOfMonth.startOf('week').startOf('day')
      const gridEnd = endOfMonth.endOf('week').startOf('day')
      const days = []
      // pad start
      // if (gridStart.isSame(startOfMonth)) {
      //   for (let i = gridStart.subtract(1, 'week'); i.isBefore(gridStart); i = i.add(1, 'day')) {
      //     days.push(i)
      //   }
      // }
      for (let i = gridStart; i.isSameOrBefore(gridEnd); i = i.add(1, 'day')) {
        days.push(i)
      }
      // pad end
      // if (gridEnd.isSame(endOfMonth)) {
      //   for (let i = gridEnd.add(1, 'day'); i.isSameOrBefore(gridEnd.add(1, 'week')); i = i.add(1, 'day')) {
      //     days.push(i)
      //   }
      // }
      return chunk(days, 7)
    },
    findInCalendar (date, calendar) {
      const weekIndex = calendar.findIndex(week => week[0].isSameOrBefore(date) && week[6].isSameOrAfter(date))
      const dayIndex = calendar[weekIndex].findIndex(day => day.isSame(date))
      return [weekIndex, dayIndex]
    },
    getCalendarDayRef (weekIndex, dayIndex) {
      return `datepicker-${this.name}-day-${weekIndex}-${dayIndex}`
    },
    focusOnDay (weekIndex, dayIndex) {
      this.$refs[this.getCalendarDayRef(weekIndex, dayIndex)][0].focus()
    },
    calendarLeft (weekIndex, dayIndex) {
      this.updatePreSelection(this.preSelection.subtract(1, 'day'))
    },
    calendarRight (weekIndex, dayIndex) {
      this.updatePreSelection(this.preSelection.add(1, 'day'))
    },
    calendarUp (weekIndex, dayIndex) {
      this.updatePreSelection(this.preSelection.subtract(1, 'week'))
    },
    calendarDown (weekIndex, dayIndex) {
      this.updatePreSelection(this.preSelection.add(1, 'week'))
    },
    calendarHome (weekIndex, dayIndex) {
      this.updatePreSelection(this.preSelection.startOf('week').startOf('day'))
    },
    calendarEnd (weekIndex, dayIndex) {
      this.updatePreSelection(this.preSelection.endOf('week').startOf('day'))
    },
    calendarPageUp (weekIndex, dayIndex) {
      const monthAgo = this.preSelection.subtract(1, 'month')
      const calendar = this.getCalendar(monthAgo)
      this.updatePreSelection(this.ensureWithinSameMonth({
        reference: monthAgo,
        calendar,
        weekIndex,
        dayIndex
      }))
    },
    calendarPageDown (weekIndex, dayIndex) {
      const nextMonth = this.preSelection.add(1, 'month')
      const calendar = this.getCalendar(nextMonth)
      this.updatePreSelection(this.ensureWithinSameMonth({
        reference: nextMonth,
        calendar,
        weekIndex,
        dayIndex
      }))
    },
    calendarShiftPageUp (weekIndex, dayIndex) {
      const yearAgo = this.preSelection.subtract(1, 'year')
      const calendar = this.getCalendar(yearAgo)
      this.updatePreSelection(this.ensureWithinSameMonth({
        reference: yearAgo,
        calendar,
        weekIndex,
        dayIndex
      }))
    },
    calendarShiftPageDown (weekIndex, dayIndex) {
      const nextYear = this.preSelection.add(1, 'year')
      const calendar = this.getCalendar(nextYear)
      this.updatePreSelection(this.ensureWithinSameMonth({
        reference: nextYear,
        calendar,
        weekIndex,
        dayIndex
      }))
    },
    ensureWithinSameMonth ({ reference, calendar, weekIndex, dayIndex }) {
      const date = calendar[weekIndex][dayIndex]
      if (date.isSame(reference, 'month')) {
        return date
      } else if (date.isBefore(reference, 'month')) {
        return calendar[weekIndex + 1][dayIndex]
      } else {
        return calendar[weekIndex - 1][dayIndex]
      }
    },
    previousYear () {
      this.updatePreSelection(this.preSelection.subtract(1, 'year'), false)
    },
    nextYear () {
      this.updatePreSelection(this.preSelection.add(1, 'year'), false)
    },
    previousMonth () {
      this.updatePreSelection(this.preSelection.subtract(1, 'month'), false)
    },
    nextMonth () {
      this.updatePreSelection(this.preSelection.add(1, 'month'), false)
    },
    selectDay (weekIndex, dayIndex) {
      const date = this.calendar[weekIndex][dayIndex]
      if (date.isSame(this.preSelection, 'month')) {
        this.input = date.format(this.inputFormat)
        this.$emit('input', date.format(this.outputFormat))
        this.$nextTick(() => {
          this.trap.deactivate()
        })
      } else {
        this.updatePreSelection(date)
      }
    },
    openDatePicker () {
      this.showDatePicker = true
      setTimeout(() => {
        this.trap.activate()
        // setTimeout because of the dialog transition
      }, 100)
    },
    updatePreSelection (newValue, focus = true) {
      const oldValue = this.preSelection
      this.preSelection = newValue
      if (!newValue.isSame(oldValue, 'month')) {
        this.calendar = this.getCalendar(newValue)
        if (focus) {
          this.$nextTick(() => {
            const [weekIndex, dayIndex] = this.findInCalendar(newValue, this.calendar)
            this.focusOnDay(weekIndex, dayIndex)
          })
        }
      } else if (focus) {
        const [weekIndex, dayIndex] = this.findInCalendar(newValue, this.calendar)
        this.focusOnDay(weekIndex, dayIndex)
      }
    },
    parseTextInput (text) {
      if (text === '') {
        this.$emit('input', undefined)
        return text
      }
      const date = dayjs(text, this.inputFormat, true)
      if (date.isValid()) {
        this.$emit('input', date.format(this.outputFormat))
      } else {
        this.$emit('input', text)
      }
      return text
    }
  }
}

</script>

<style lang="scss" scoped>
@import "@/assets/scss/utilities";

@mixin icon-calendar($color) {
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 13 13' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cpath d='M.98 6h11.37M10.1 1.95h2.01c.15 0 .24.1.24.23v8.89s0 1.24-1.24 1.24h-8.9c-1.23 0-1.23-1.1-1.23-1.24V2.18c0-.14.1-.23.24-.23h2.13m1.07 0h4.5' stroke='#{$color}' stroke-width='1.3'/%3E%3Cpath d='M3.88.2c.39 0 .7.26.7.58v2.34c0 .32-.31.58-.7.58-.38 0-.7-.26-.7-.58V.78c0-.32.32-.58.7-.58zM9.48.2c.39 0 .7.26.7.58v2.34c0 .32-.31.58-.7.58-.38 0-.7-.26-.7-.58V.78c0-.32.32-.58.7-.58z' fill='#{$color}'/%3E%3C/g%3E%3C/svg%3E");
}

.f-field--date {
  position: relative;
  .f-field {
    margin: 0;
    position: relative;

    .f-field__input-wrapper {
      border: none;
      padding: 0;
      margin: 0;
      position: relative;

      .f-field__input {
        padding-right: 2.5rem;
      }
    }
  }
}

.f-field--underline + .f-field__dialog {
  margin-top: 0.5rem;
}

.f-field__dialog-trigger {
  height: 1rem;
  width: 1rem;
  position: absolute;
  top: 1rem;
  right: 1rem;
  transition-property: all;
  @include icon-calendar($color: encodecolor($gray-400));
  @include mdTransition;

  background-repeat: no-repeat;
  background-position: center right;

  &:hover,
  &:focus {
    outline: none;
    @include icon-calendar($color: encodecolor($blue));
  }

  &:disabled {
    opacity: 0.5;
    pointer-events: none;
  }
}

$dialog-width: 300px;

.f-field__dialog {
  padding: 24px;
  width: $dialog-width;
  position: absolute;
  background: white;
  box-shadow: 0 2px 20px 0 rgb(0 0 0 / 15%);
  border-radius: 3px;
  z-index: 998;
  font-size: 1rem;
  margin-top: -1rem;
  left: calc(50% - #{$dialog-width} / 2);
  top: 100%;
  opacity: 1;
  visibility: visible;
  transform: translateY(0) scale(1);
  transition: all 0.5s cubic-bezier(0.645, 0.045, 0.355, 1);

  &::before {
    position: absolute;
    content: "";
    top: -0.5rem;
    left: calc(50% - 10px);
    filter: drop-shadow(0 -2px 1px rgb(0 0 0 / 5%));
    width: 0;
    height: 0;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-bottom: 8px solid #fff;
  }

  &.f-field__dialog--hidden {
    opacity: 0;
    visibility: hidden;
    transform: translateY(5%) scale(0.95);
  }
}

.calendar__week,
.calendar__weekdays,
.dialog__year-month-select {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.dialog__year-month-select {
  margin-bottom: 1rem;
  font-size: 1.125rem;

  .dialog__year-month {
    color: black;
    font-family: $primary-font;
    font-weight: 800;
  }

  .arrow {
    width: 20px;
    height: 20px;
    position: relative;
    &.arrow--previous {
      transform: translateX(-5px) translateY(-1px) rotate(45deg);
    }
    &.arrow--next {
      transform: translateX(5px) translateY(1px) rotate(225deg);
    }
    &::before,
    &::after {
      content: "";
      width: 2px;
      height: 8px;
      background: $blue;
      position: absolute;
      top: 6px;
      left: 7px;
      border-radius: 2px;
      transform-origin: bottom left;
    }
    &::after {
      transform: rotate(90deg) translateX(-2px);
    }
  }
}

.dialog__calendar {
  .calendar__month {
    padding-top: 4px;
    margin-top: 4px;
    border-top: 1px solid rgba($gray-400, 0.4);
  }
  .calendar__weekdays {
    color: $gray-400;
    font-weight: 600;
  }
  .calendar__weekday {
    margin: 0.25rem 0;
  }

  .calendar__weekday,
  .calendar__day {
    border: none;
    background: none;
    margin: 0.5rem 0;
    box-sizing: border-box;
    width: 0.8925rem;
  }
  .calendar__day {
    position: relative;
    display: flex;
    justify-content: center;
    font-weight: 600;

    &:hover,
    &:focus {
      outline: none;
      color: black;
      &::before {
        content: "";
        background: rgba($gray-400, 0.4);
        width: 26px;
        height: 26px;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translateY(-50%) translateX(-50%);
        z-index: -1;
        border-radius: 2px;
      }
    }

    &.calendar__day--disabled {
      color: $gray-400;
      opacity: 0.5;
    }

    &.calendar__day--selected {
      color: white;
      &::before {
        content: "";
        background: $blue;
        width: 26px;
        height: 26px;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translateY(-50%) translateX(-50%);
        z-index: -1;
        border-radius: 100%;
      }
    }
  }
}
</style>
