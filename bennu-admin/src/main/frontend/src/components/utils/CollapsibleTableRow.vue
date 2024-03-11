<template>
  <transition
    name="fade-and-shrink"
    fade
  >
    <component
      :is="as"
      v-if="open"
      v-bind="$attrs"
    >
      <slot />
    </component>
  </transition>
</template>

<script>
export default {
  name: 'CollapsibleTableRow',
  props: {
    as: {
      type: String,
      required: false,
      default: () => 'tr'
    },
    open: {
      type: Boolean,
      required: false,
      default: () => false
    }
  }
}
</script>
