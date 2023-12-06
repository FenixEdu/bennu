<template>
  <div
    :style="iconCustomStyles"
    :class="`icon--${iconSize}`"
    class="lyb-icon"
    v-bind="$attrs"
  >
    <component :is="IconComponent" />
  </div>
</template>

<script>
export default {
  name: 'Icon',
  props: {
    name: {
      type: String,
      required: true
    },
    iconSize: {
      type: String,
      required: false,
      default: 'md'
    },
    iconBackground: {
      type: String,
      required: false,
      default: 'transparent'
    },
    iconColor: {
      type: String,
      required: false,
      default: 'inherit'
    }
  },
  computed: {
    IconComponent () {
      return () => import(/* webpackChunkName: "icon" */ '@/components/iconography/icons/' + this.name)
    },
    iconCustomStyles () {
      return {
        color: this.iconColor,
        'background-color': this.iconBackground
      }
    }
  }
}
</script>

<style lang="scss">
.lyb-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s ease;
  border-radius: 100%;
  svg {
    width: 100%;
    height: auto;
    overflow: visible;
  }
}
.icon--sm {
  width: 3rem;
  * {
    stroke-width: 1.4px !important;
  }
}
.icon--flag {
  width: 2rem;
}
</style>
