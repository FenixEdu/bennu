<template>
  <p>
    <template v-if="isEmpty">
      <strong>{{ translate(field.label) }}:</strong>
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
      <!-- FIXME: is it generic if we assume the submit value has the "downloadUrl" property? -->
      <a
        :class="{ 'u-active-link': submitValue.downloadUrl, 'u-text-disabled': !submitValue.downloadUrl }"
        :href="submitValue.downloadUrl"
      >
        <span>
          {{ translate(field.label) }}
        </span>
        <img
          v-if="submitValue.downloadUrl"
          src="~@/assets/images/external-link.svg"
          aria-hidden="true"
        >
      </a>
    </template>
  </p>
</template>

<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'

export default {
  name: 'FileField',
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
    submitValue () {
      return this.snapshot.value
    },
    isEmpty () {
      return this.snapshot.isEmpty
    }
  }
}
</script>
