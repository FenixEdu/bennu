<template>
  <div class="empty-state">
    <div class="empty-state__illustration">
      <icon
        name="paper-and-pen"
        icon-color="var(--blue)"
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
  }
}
</script>
