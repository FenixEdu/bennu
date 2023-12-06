<template>
  <p v-if="$v.$error">
    <strong>{{ translate(field.label) }}:</strong>
    <template v-if="($v.$params.minLength || $v.$params.required) && (!$v.required || !$v.minLength)">
      {{ $tc('dynamic-form.fields.array.errors.min-length', $v.$params.minLength.min, { min: $v.$params.minLength.min }) }}
    </template>
    <template v-else>
      <ol class="l-list l-list--numbered">
        <template v-for="(item, index) in snapshot.items">
          <li
            v-if="$v[item.key].$error"
            :key="item.key"
            class="l-list__item"
            :aria-label="$t('item-index', { index: index + 1 })"
          >
            <span aria-hidden="true"> {{ $t('item-index', { index: index + 1}) }}.</span>
            <template v-for="fieldSnapshot in item.properties">
              <component
                :is="ErrorFields[fieldSnapshot.field.type]"
                :key="fieldSnapshot.field.field"
                :snapshot="fieldSnapshot"
              />
            </template>
          </li>
        </template>
      </ol>
    </template>
  </p>
</template>

<script>
import TranslateApiString from '@/mixins/TranslateApiString'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'

export default {
  mixins: [
    TranslateApiString
  ],
  inject: {
    FormFieldClass: {
      default: () => DynamicFormField
    }
  },
  props: {
    snapshot: {
      type: Object,
      required: true
    }
  },
  i18n: {
    messages: {
      pt: {
        'item-index': 'Elemento {index}'
      },
      en: {
        'item-index': 'Item {index}'
      }
    }
  },
  computed: {
    field () {
      return this.snapshot.field
    },
    $v () {
      return this.snapshot.$v
    },
    ErrorFields () {
      return this.FormFieldClass.ErrorFields
    }
  },
  created () {
    // Register local components dynamically
    // We do this in the created() lifecycle hook because the components must be registered before mount
    Object.assign(this.$options.components, this.FormFieldClass.ErrorComponents)
  }
}
</script>
<style lang="scss" scoped>
.l-list {
  margin-top: 4px;
}
.l-list--numbered {
  margin-left: 1rem;
}
</style>
