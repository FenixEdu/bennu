<template>
  <div
    class="empty-state"
    :class="{ 'empty-state--embedded': layout === 'embedded'}"
  >
    <div class="empty-state__illustration">
      <icon
        :name="icon"
        icon-color="var(--gray)"
        aria-hidden="true"
      />
    </div>
    <p class="h4 empty-state__title">
      <slot name="title" />
    </p>
    <p class="empty-state__description">
      <slot name="description" />
    </p>
    <slot />
  </div>
</template>

<script>
import Icon from '@/components/utils/Icon.vue'

export default {
  components: {
    Icon
  },
  props: {
    icon: {
      type: String,
      default: 'sad-face'
    },
    layout: {
      type: String,
      default: undefined
    }
  }
}
</script>
