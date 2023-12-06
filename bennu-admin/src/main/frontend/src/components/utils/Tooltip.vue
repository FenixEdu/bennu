<template>
  <!-- TODO: make this accessible https://www.w3.org/TR/wai-aria-practices/#tooltip -->
  <div
    v-if="$scopedSlots.trigger"
    class="tooltip"
    :class="tooltipClasses"
    @mouseleave="closeTooltip"
  >
    <slot
      name="trigger"
      :attrs="{ 'class': 'tooltip__trigger' }"
      :events="{ 'click': toggleTooltip, 'blur': closeTooltip }"
    />
    <div
      v-if="$scopedSlots.message"
      ref="tooltipbox"
      class="tooltip__box"
      :style="{ 'top' : resultingTop }"
    >
      <div class="tooltip__container">
        <slot name="message" />
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data () {
    return {
      left: true,
      isTooltipOpen: false,
      resultingTop: undefined
    }
  },
  computed: {
    tooltipClasses () {
      return [
        this.left ? 'tooltip--opens-to-left' : 'tooltip--opens-to-right',
        this.isTooltipOpen ? 'tooltip--open' : ''
      ]
    }
  },
  mounted () {
    this.setPosition()
    window.addEventListener('resize', this.setPosition)
  },
  beforeDestroy: function () {
    window.removeEventListener('resize', this.setPosition)
  },
  methods: {
    toggleTooltip () {
      this.isTooltipOpen = !this.isTooltipOpen
    },
    openTooltip () {
      this.isTooltipOpen = true
    },
    closeTooltip () {
      this.isTooltipOpen = false
    },
    setPosition () {
      if (this.$scopedSlots.message) {
        const elLeft = this.$refs.tooltipbox.getBoundingClientRect().left
        const elRight = this.$refs.tooltipbox.getBoundingClientRect().right
        const elHeight = this.$refs.tooltipbox.getBoundingClientRect().height
        const buttonHeight = this.$el.getBoundingClientRect().height
        this.resultingTop = `${(elHeight / 2 * -1) + (buttonHeight / 2)}px`
        const windowWidth = document.body.clientWidth
        if (windowWidth <= elRight || elLeft <= 0) {
          this.left = !this.left
        }
      }
    }
  }
}
</script>
<style lang="scss">
@import "@/assets/scss/variables";

.tooltip {
  position: relative;
  width: 1.25em;
  height: 1.25em;
}
.tooltip__trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
  font-weight: 700;
  width: 100%;
  height: 100%;
  border-radius: 100%;
  color: $light-blue-700;
  border: 2px solid currentcolor;
  background-color: rgba($blue-600, 0);
  transition-property: background-color, color, border-color;
  @include mdTransition;

  &:hover,
  &:focus {
    color: $blue;
    border-color: $blue;
    outline: none;
    box-shadow: none;
  }
}
.tooltip__box {
  position: absolute;
  z-index: 10000;
  width: calc(100vw - 4rem);
  padding: 1rem 0.75rem 1rem 1.5rem;
  border-radius: 6px;
  background-color: white;
  box-shadow: 0 0 3px 0 rgb(0 0 0 / 10%), 0 5px 10px 0 rgb(0 0 0 / 15%);
  right: calc(100% + 1rem);
  top: 0;
  visibility: hidden;
  opacity: 0;
  transform: translateY(-1rem);
  pointer-events: none;
  transition-property: transform, opacity;
  @include mdTransition;

  &::before {
    content: "";
    left: 100%;
    top: calc(50% - 0.75rem);
    position: absolute;
    width: 0;
    height: 0;
    border-style: solid;
    border-width: 0.75rem 0 0.75rem 1rem;
    border-color: transparent transparent transparent white;
    filter: drop-shadow(5px 0 3px rgb(0 0 0 / 10%));
    line-height: 0;
  }
}
@media screen and (min-width: 50rem) {
  .tooltip__box {
    width: 21.875rem;
  }
}
.tooltip__container {
  padding-right: 0.75rem;
  max-height: 9rem;
  overflow-x: hidden;
  overflow-y: auto;
  &::-webkit-scrollbar-track {
    background-color: $gray-300;
  }

  &::-webkit-scrollbar {
    width: 0.125rem;
    background-color: $gray-300;
    height: 0.25rem;
  }

  &::-webkit-scrollbar-thumb {
    background-color: $gray-400;
  }
}

.tooltip--opens-to-right {
  .tooltip__box {
    left: calc(100% + 1rem);
    transform: translateX(-0.5rem);
    right: unset;
    &::before {
      right: 100%;
      left: unset;
      border-width: 0.75rem 1rem 0.75rem 0;
      border-color: transparent white transparent transparent;
    }
  }
}
.tooltip--opens-to-left {
  .tooltip__box {
    right: calc(100% + 1rem);
    transform: translateX(0.5rem);
    left: unset;
    &::before {
      left: 100%;
      right: unset;
      border-width: 0.75rem 0 0.75rem 1rem;
      border-color: transparent transparent transparent white;
    }
  }
}
.tooltip--open .tooltip__trigger {
  color: white;
  background-color: rgba($blue-600, 1);
  &:focus {
    border-color: $blue-700;
  }
}
.tooltip--open .tooltip__box {
  visibility: visible;
  opacity: 1;
  transform: translateX(0);
  pointer-events: all;
}
</style>
