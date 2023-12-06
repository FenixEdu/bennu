<template>
  <div
    :style="{'color': iconColor}"
    :class="[{'animated-icon--is-playing': isPlaying}, `animated-icon--${iconSize}`]"
    class="animated-icon"
  >
    <component :is="IconComponent" />
  </div>
</template>

<script>
export default {
  name: 'AnimatedIcon',
  props: {
    isPlaying: {
      type: Boolean,
      required: false,
      default: false
    },
    iconColor: {
      type: String,
      required: false,
      default: 'var(--blue)'
    },
    iconSize: {
      type: String,
      required: false,
      default: 'md'
    },
    name: {
      type: String,
      required: true
    }
  },
  computed: {
    IconComponent () {
      return () => import(
        /* webpackChunkName: "animated-icon" */ '@/components/iconography/animated-icons/' + this.name
      )
    }
  }
}
</script>

<style lang="scss">
.animated-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 7.5rem;
  height: 7.5rem;
  margin: 1rem;
  transition: color 2s ease;
}
svg {
  overflow: visible;
}
.animated-icon--sm {

  * {
    stroke-width: 1.4px !important;
  }
}
</style>
