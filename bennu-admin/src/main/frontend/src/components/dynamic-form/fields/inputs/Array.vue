<template>
  <fieldset
    class="f-fieldset array__container"
    :aria-labelledby="`array-${$attrs.name || field.field}-label`"
    :aria-describedby="field.description ? `array-${$attrs.name || field.field}-description` : undefined"
    :aria-invalid="String($v.$error)"
    :aria-errormessage="$v.$error ? `array-${$attrs.name || field.field}-error` : undefined"
    :aria-required="String(field.required)"
  >
    <div
      class="card-row array__header f-field"
      :class="{ 'f-field--danger': $v.$error, 'f-field--required': field.required }"
    >
      <legend
        :id="`array-${$attrs.name || field.field}-label`"
        class="f-field__label"
      >
        {{ translate(field.label) }}
      </legend>
      <p
        v-if="field.description"
        :id="`array-${$attrs.name || field.field}-description`"
        class="f-field__description"
      >
        {{ translate(field.description) }}
      </p>
      <div
        v-if="$v.$error"
        :id="`array-${$attrs.name || field.field}-error`"
        class="f-field__validation"
        role="alert"
      >
        <template v-if="($v.$params.minLength || $v.$params.required) && (!$v.required || !$v.minLength)">
          {{ $tc('dynamic-form.fields.array.errors.min-length', $v.$params.minLength.min, { min: $v.$params.minLength.min }) }}
        </template>
      </div>
      <button
        v-if="Object.keys($v.$model).length === 0"
        ref="add-item-btn"
        class="btn--link"
        @click.prevent="addItem"
      >
        <span
          class="btn__icon"
          aria-hidden="true"
        >
          <svg
            width="9"
            height="9"
            viewBox="0 0 11 11"
            xmlns="http://www.w3.org/2000/svg"
          >
            <g
              fill="currentColor"
              fill-rule="evenodd"
            >
              <rect
                x=".8033009"
                y="4.3033009"
                width="10"
                height="2"
                rx="1"
              />
              <rect
                transform="rotate(90 5.801068 5.301068)"
                x=".8010678"
                y="4.3010678"
                width="10"
                height="2"
                rx="1"
              />
            </g>
          </svg>
        </span>
        <span>
          {{ translate(field.labelAddElement) || $t('button-add-item-default') }}
        </span>
      </button>
    </div>
    <ol
      class="array__items"
      aria-live="assertive"
      aria-relevant="additions removals"
    >
      <li
        v-for="(item, itemIndex) in formField.items"
        ref="items"
        :key="item[formField.TRACK_ID_FIELD]"
        class="array__list-item"
        :aria-labelledby="`array-${$attrs.name || field.field}-item${itemIndex}`"
        tabindex="-1"
      >
        <div class="card-row card-row--sm array__index">
          <div class="card-row__text">
            <p
              :id="`array-${$attrs.name || field.field}-item${itemIndex}`"
              class="h6 card-row__title u-text-gray"
            >
              {{ $t('item-index', { index: itemIndex + 1}) }}
            </p>
          </div>
          <div class="card-row__meta">
            <button
              class="btn--danger btn--icon btn--remove-item"
              :aria-label="$t('aria.button-remove-item', { index: itemIndex + 1 })"
              @click.prevent="removeItem(item, itemIndex)"
            >
              <svg
                viewBox="0 0 16 16"
                width="16"
                height="16"
                xmlns="http://www.w3.org/2000/svg"
              >
                <g
                  fill="none"
                  fill-rule="evenodd"
                >
                  <circle
                    fill="currentColor"
                    cx="8"
                    cy="8"
                    r="8"
                  />
                  <path
                    stroke="#FFF"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M5 8h6"
                  />
                </g>
              </svg>
            </button>
          </div>
        </div>
        <div
          v-for="subFormField in item.fields"
          v-show="subFormField.exists()"
          :key="subFormField.field.field"
          class="card-row"
        >
          <readonly-field-wrapper
            :form-field="subFormField"
            class="array__field"
            :name="`${$attrs.name || field.field}-${subFormField.field.field}-item${itemIndex}`"
          />
        </div>
      </li>
    </ol>
    <div
      v-if="Object.keys($v.$model).length > 0"
      class="card-row array__footer"
    >
      <div class="array__item">
        <button
          class="btn--link btn--lg"
          :disabled="hasMaxItems"
          :aria-describedby="hasMaxItems ? `array-${$attrs.name || field.field}-max-items` : undefined"
          @click.prevent="addItem"
        >
          <span
            class="btn__icon"
            aria-hidden="true"
          >
            <svg
              width="9"
              height="9"
              viewBox="0 0 11 11"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g
                fill="currentColor"
                fill-rule="evenodd"
              >
                <rect
                  x=".8033009"
                  y="4.3033009"
                  width="10"
                  height="2"
                  rx="1"
                />
                <rect
                  transform="rotate(90 5.801068 5.301068)"
                  x=".8010678"
                  y="4.3010678"
                  width="10"
                  height="2"
                  rx="1"
                />
              </g>
            </svg>
          </span>
          <span>
            {{ translate(field.labelAddElement) || $t('button-add-item-default') }}
          </span>
        </button>
        <p
          v-if="hasMaxItems"
          :id="`array-${$attrs.name || field.field}-max-items`"
          class="small"
          style="margin-top: 0.5rem;"
        >
          {{ $tc('dynamic-form.fields.array.errors.max-length', $v.$params.maxLength.max, { max: $v.$params.maxLength.max }) }}
        </p>
      </div>
    </div>
  </fieldset>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import { withParams, req } from 'vuelidate/lib/validators/common'
import Vue from 'vue'
import isEqual from 'underscore/modules/isEqual'
import ReadonlyFieldWrapper from '@/components/dynamic-form/fields/inputs/helpers/ReadonlyFieldWrapper.vue'

const minLength = min => withParams(
  { type: 'minLength', min },
  value => !req(value) || Object.keys(value).length >= min
)
const maxLength = max => withParams(
  { type: 'maxLength', max },
  value => !req(value) || Object.keys(value).length <= max
)

export const extend = superclass => class extends superclass {
  constructor ({ field, submitValue, uniqueId, location, form, parent }, options) {
    super({ field, submitValue, uniqueId, location, form, parent }, options)
    this.extraArgs = options
    this.items = Array.isArray(submitValue ?? [])
      ? (submitValue ?? []).map(element => {
          const trackId = this.getVuelidateTrackId()
          return {
            [this.TRACK_ID_FIELD]: trackId,
            fields: this.field.properties.map(
              subfield => this.constructor.getInstance({
                field: subfield,
                submitValue: element?.[subfield.field] ?? undefined,
                uniqueId: this.getSubfieldUniqueId(subfield, trackId),
                location: { ...this.location, [this.field.field]: trackId },
                form: this.form,
                parent: this
              }, this.extraArgs)
            )
          }
        })
      : []
  }

  setVuelidate (vuelidate, reset) {
    super.setVuelidate(vuelidate, reset)
    this.items.forEach(item => {
      item.fields.forEach(formField => {
        const $v = this.$v[item[this.TRACK_ID_FIELD]]?.[formField.field.field]
        formField.setVuelidate($v, reset)
      })
    })
  }

  getSubfieldUniqueId (subfield, index) {
    return `${this.uniqueId}-${subfield.field}-item${index}`
  }

  get emptyValue () {
    return {}
  }

  isEmpty () {
    return this.$v.$model === undefined || isEqual(this.$v.$model, this.emptyValue)
  }

  get TRACK_ID_FIELD () { return '$__uuid__' }

  $__TRACK_ID_INDEX = 0
  getVuelidateTrackId () {
    return String(this.$__TRACK_ID_INDEX++)
  }

  setVuelidateTrackId = item => {
    item[this.TRACK_ID_FIELD] = item[this.TRACK_ID_FIELD] ?? this.getVuelidateTrackId()
  }

  get validations () {
    const validations = {}
    if (this.field.required) {
      validations.required = requiredIf(() => this.exists())
      validations.minLength = minLength(1)
    }
    if (this.field.min !== undefined) {
      validations.minLength = minLength(this.field.min)
    }
    if (this.field.max !== undefined) {
      validations.maxLength = maxLength(this.field.max)
    }

    // we can't make use of vuelidate's $each because we're using OOP
    // and we want each element to correspond to its own **actual** form field
    // including validations. $each would use **the same** validations object for
    // every element of the array, which wouldn't allow us to use OOP
    // (bc validations may use `this`, which would be the same for every element if we used $each)
    this.items.forEach(item => {
      validations[item[this.TRACK_ID_FIELD]] = Object.fromEntries(
        item.fields.map(subFormField => [subFormField.field.field, subFormField.validations])
      )
    })
    return validations
  }

  parseSubmitValue () {
    if (!Array.isArray(this.previousSubmitValue)) {
      return this.emptyValue
    }

    return Object.fromEntries(
      this.items.map(item => [
        /* key */ item[this.TRACK_ID_FIELD],
        /* value */ Object.fromEntries(
          item.fields.map(formField => [formField.field.field, formField.parseSubmitValue()])
        )
      ])
    )
  }

  async getSubmitBlobs () {
    if (!this.exists()) {
      return {}
    }
    const submitBlobs = {}
    for (const i in this.items) {
      const item = this.items[i]
      for (const subFormField of item.fields) {
        const fieldBlobs = await subFormField.getSubmitBlobs()
        Object.entries(fieldBlobs || {})
          .forEach(([key, blob]) => {
            submitBlobs[`.${i}.${subFormField.field.field}${key}`] = blob
          })
      }
    }
    return submitBlobs
  }

  async getSubmitValue () {
    if (!this.exists()) {
      return undefined
    }
    const submitValue = []
    for (const item of this.items) {
      const fieldValue = {}
      for (const subFormField of item.fields) {
        fieldValue[subFormField.field.field] = await subFormField.getSubmitValue()
      }
      submitValue.push(fieldValue)
    }
    return submitValue
  }

  async setPreviousSubmitValue (value) {
    this.previousSubmitValue = value
    this.items.forEach((item, itemIndex) => {
      const itemValue = value[itemIndex]
      item.fields.forEach(subFormField => subFormField.setPreviousSubmitValue(itemValue[subFormField.field.field]))
    })
  }

  get value () {
    return this.items.map(item =>
      Object.fromEntries(item.fields.map(subFormField => [subFormField.field.field, subFormField.value]))
    )
  }

  get simpleValue () {
    return this.items.map(item =>
      Object.fromEntries(item.fields.map(subFormField => [subFormField.field.field, subFormField.simpleValue]))
    )
  }

  addItem (submitValue = null) {
    const item = {}
    this.setVuelidateTrackId(item)
    item.fields = this.field.properties.map(
      subfield => this.constructor.getInstance({
        field: subfield,
        submitValue: submitValue?.[subfield.field] ?? undefined,
        uniqueId: this.getSubfieldUniqueId(subfield, item[this.TRACK_ID_FIELD]),
        location: { ...this.location, [this.field.field]: item[this.TRACK_ID_FIELD] },
        form: this.form
      }, this.extraArgs)
    )
    this.items.push(item)
    // init with empty value
    const itemModel = Object.fromEntries(
      item.fields.map(subFormField => [subFormField.field.field, subFormField.emptyValue])
    )
    /*
      For all that is holy and sacred, DO NOT change the line below.
      If we don't assign to this.$v.$model with Vue, reactivity is broken.
      Don't say I didn't warn you when you waste 8 hours trying to figure out what's wrong.

      Sincerely,
      Someone who's wasted 8 hours trying to figure out what was wrong <3
    */
    Vue.set(this.$v.$model, item[this.TRACK_ID_FIELD], itemModel)

    // FIXME: setting a property doesn't touch the form
    // consequence: if someone adds an item and leaves the page without touching a field, it will not be prevented
  }

  removeItem (item) {
    const itemId = item[this.TRACK_ID_FIELD]
    const index = this.items.findIndex(el => el[this.TRACK_ID_FIELD] === itemId)
    this.items.splice(index, 1)
    Vue.delete(this.$v.$model, itemId)

    // FIXME: deleting a property doesn't touch the form
    // consequence: if someone deletes an item and leaves the page without touching a field, it will not be prevented
  }

  findField ({ name, pivot }) {
    if (pivot !== undefined && this.field.field in pivot.location) {
      const itemId = pivot.location[this.field.field]
      const item = this.items.find(item => item[this.TRACK_ID_FIELD] === itemId)
      for (const subFormField of item.fields) {
        const field = subFormField.findField({ name, pivot: this })
        if (field) return field
      }
    }
    return super.findField({ name, pivot })
  }

  takeSnapshot () {
    const { snapshot, destroy } = super.takeSnapshot()
    const destroyers = []
    const items = []
    for (const item of this.items) {
      const itemSnapshot = {}
      for (const formField of item.fields) {
        const { snapshot: fieldSnapshot, destroy: destroyField } = formField.takeSnapshot()
        destroyers.push(destroyField)
        itemSnapshot[formField.field.field] = fieldSnapshot
      }
      items.push({ key: item[this.TRACK_ID_FIELD], properties: itemSnapshot })
    }

    return {
      snapshot: {
        ...snapshot,
        items
      },
      async destroy () {
        return Promise.all([
          destroy(),
          ...destroyers.map(d => d())
        ])
      }
    }
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'ArrayField',
  components: {
    ReadonlyFieldWrapper
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
  i18n: {
    messages: {
      pt: {
        'button-add-item-default': 'Adicionar elemento',
        'item-index': 'Elemento {index}',
        aria: {
          'button-remove-item': 'Remover elemento {index}'
        }
      },
      en: {
        'button-add-item-default': 'Add item',
        'item-index': 'Item {index}',
        aria: {
          'button-remove-item': 'Remove item {index}'
        }
      }
    }
  },
  computed: {
    field () {
      return this.formField.field
    },
    $v () {
      return this.formField.$v
    },
    hasMaxItems () {
      return this.$v.$params.maxLength && Object.keys(this.$v.$model).length >= this.$v.$params.maxLength.max
    }
  },
  methods: {
    addItem () {
      this.formField.addItem()
      this.$nextTick(() => {
        const newItemEl = this.$refs.items?.[Object.keys(this.$v.$model).length - 1]
        newItemEl && newItemEl.focus()
      })
    },
    removeItem (item, index) {
      this.formField.removeItem(item)
      this.$nextTick(() => {
        const nextItemEl = this.$refs.items?.[index]
        const prevItemEl = this.$refs.items?.[index - 1]
        if (nextItemEl) {
          nextItemEl.focus()
        } else if (prevItemEl) {
          prevItemEl.focus()
        } else {
          this.$refs['add-item-btn'].focus()
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
@import "@/assets/scss/variables";
.array__container {
  display: flex;
  flex-direction: column;
  flex: 1;
  margin: -1.5rem !important;
  &.f-field.f-field--required > .array__header {
    .f-field__label::after {
      content: "*";
      color: $magenta;
      display: inline;
    }
  }

  .array__footer,
  .array__index {
    border-top: 1px solid $light-gray;
  }

  .array__footer,
  .array__header,
  .array__field {
    margin-top: 0;
    box-sizing: border-box;
    position: relative;
  }

  .array__header {
    margin-bottom: 0;
    display: flex;
    flex-direction: column;
    align-items: flex-start;

    .f-field__label + .f-field__description {
      margin-top: 0.25rem;
      margin-bottom: 0;
    }

    .btn--link {
      margin-top: 1rem;
    }
  }

  .array__list-item:focus:not(:focus-visible) {
    /* Remove the focus indicator on mouse-focus for browsers
      that support :focus-visible */
    outline: none;
  }

  .btn--remove-item {
    padding: 0.25rem;
  }
}

</style>
