<template>
  <div
    v-if="field.allowOther"
    class="f-group"
  >
    <slot
      :attrs="{
        disabled: $v.$model.showOtherOption,
        'aria-describedby': !$v.$model.showOtherOption ? `${$attrs.name || field.field}-insert-other-option` : undefined
      }"
    />
    <i18n
      v-show="!$v.$model.showOtherOption"
      :id="`${$attrs.name || field.field}-insert-other-option`"
      path="dynamic-form.fields.select.allow-other.actions.insert.description"
      tag="p"
      class="small"
    >
      <template #action>
        <button
          type="button"
          class="p--default btn--link"
          style="display: inline; font-size: unset;"
          @click.prevent="$v.$model.showOtherOption = true"
        >
          {{ $t('dynamic-form.fields.select.allow-other.actions.insert.action') }}
        </button>
      </template>
    </i18n>
    <text-input
      v-show="$v.$model.showOtherOption"
      ref="other-option"
      v-model="$v.otherOption.$model"
      :name="`${$attrs.name || field.field}-other-option`"
      :required="$v.$model.showOtherOption && field.required"
      :aria-describedby="`${$attrs.name || field.field}-other-option-description ${$attrs.name || field.field}-remove-other-option`"
      :label="$t('dynamic-form.fields.select.allow-other.label')"
      :invalid="$v.otherOption.$error"
      variant="outlined"
    >
      <template #error-message>
        {{ $t('dynamic-form.fields.text.errors.required') }}
      </template>
    </text-input>
    <p
      v-show="$v.$model.showOtherOption"
      :id="`${$attrs.name || field.field}-other-option-description`"
      class="sr-only"
    >
      {{ $t('dynamic-form.fields.select.allow-other.aria-description', { name: translate(field.label) }) }}
    </p>
    <i18n
      v-show="$v.$model.showOtherOption"
      :id="`${$attrs.name || field.field}-remove-other-option`"
      path="dynamic-form.fields.select.allow-other.actions.remove.description"
      tag="p"
      class="small"
    >
      <template #action>
        <button
          type="button"
          class="p--default btn--link btn--danger"
          style="display: inline; font-size: unset;"
          @click.prevent="$v.$model.showOtherOption = false"
        >
          {{ $t('dynamic-form.fields.select.allow-other.actions.remove.action') }}
        </button>
      </template>
    </i18n>
  </div>
  <div
    v-else
    class="f-group"
  >
    <slot />
  </div>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'

export default {
  components: {
    TextInput: () => import('@/components/utils/fields/TextInput.vue')
  },
  mixins: [
    TranslateApiStringMixin
  ],
  props: {
    formField: {
      type: Object,
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
  watch: {
    '$v.$model.showOtherOption' (value) {
      if (value) {
        this.$nextTick(() => {
          const ref = this.$refs['other-option']
          // FIXME: this will break if TextInput ever changes the name of the ref
          // but we really need to switch focus back to the input, it would be bad ux otherwise
          const otherOptionInput = ref.$refs.input
          otherOptionInput.focus()
        })
      } else {
        this.$v.otherOption.$model = ''
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.f-group {
  width: 100%;
}
</style>
