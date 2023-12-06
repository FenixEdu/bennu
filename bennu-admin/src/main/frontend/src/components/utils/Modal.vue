<template>
  <transition name="modal-slide-down">
    <div
      v-if="value"
      :id="id"
      class="modal"
      role="dialog"
      aria-modal="true"
      v-bind="$attrs"
      @keydown.esc.stop="!preventClose && trap.deactivate()"
    >
      <button
        v-if="hasCloseButton && !preventClose"
        role="button"
        class="modal__close"
        :aria-label="$t('close-button-aria-label')"
        @click.prevent="close"
      >
        <svg
          viewBox="0 0 40 40"
          xmlns="http://www.w3.org/2000/svg"
        >
          <g
            fill="none"
            fill-rule="evenodd"
          >
            <circle
              stroke="#DDE4E9"
              stroke-width="2.5"
              fill="#DDE4E9"
              cx="20"
              cy="20"
              r="18.75"
            />
            <g
              opacity=".5"
              stroke="#1C172F"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
            >
              <path d="M14.12 14.12l11.64 11.64M25.76 14.12L14.12 25.76" />
            </g>
          </g>
        </svg>
      </button>
      <div class="modal__panel">
        <div
          :id="`${id}-modal-container`"
          class="modal__container u-a11y-focusable"
          tabindex="-1"
        >
          <slot name="modal-panel" />
        </div>
      </div>
      <footer
        v-if="!!$slots['modal-footer']"
        class="modal__footer"
      >
        <slot name="modal-footer" />
      </footer>
    </div>
  </transition>
</template>

<script>
import { createFocusTrap } from 'focus-trap'

export default {
  name: 'Modal',
  i18n: {
    messages: {
      pt: {
        'close-button-aria-label': 'Fechar janela de diálogo'
      },
      en: {
        'close-button-aria-label': 'Close dialog'
      }
    }
  },
  props: {
    value: {
      type: Boolean,
      required: true
    },
    hasCloseButton: {
      type: Boolean,
      default: true
    },
    id: {
      type: String,
      default: () => `modal-${Math.floor(Math.random() * 10000)}`
    },
    initialFocus: {
      type: String,
      default: undefined
    },
    returnFocus: {
      type: String,
      default: undefined
    },
    preventClose: {
      // whether user-initiated close actions should be prevented
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      trap: undefined
    }
  },
  watch: {
    value: {
      immediate: true,
      handler (newValue) {
        if (newValue === true) {
          document.body.classList.add('scroll-lock--modal')
          this.$nextTick(() => {
            // we need to update container elements because we use v-if,
            // which removes the element from the DOM when the modal closes
            this.trap.updateContainerElements(`#${this.id}`)
            this.trap.activate()
          })
        } else {
          this.$nextTick(() => {
            this.trap.deactivate()
          })
          document.body.classList.remove('scroll-lock--modal')
        }
      }
    }
  },
  mounted () {
    this.trap = createFocusTrap(`#${this.id}`, {
      // we can't rely on focus-trap to deactivate when Escape is pressed
      // because it captures the event and handles it before any children of the modal can handle it
      // effectively closing the modal before any children have the chance to handle the Escape event.
      // we want the Escape event to bubble up, not trickle down
      escapeDeactivates: false,
      clickOutsideDeactivates: () => !this.preventClose,
      initialFocus: this.initialFocus,
      fallbackFocus: `#${this.id}-modal-container`,
      setReturnFocus: previousActiveElement => {
        if (this.returnFocus) {
          return document.querySelector(this.returnFocus) ?? previousActiveElement
        } else {
          return previousActiveElement
        }
      },
      onDeactivate: () => {
        this.close()
      }
    })
  },
  beforeDestroy () {
    this.trap.deactivate()
  },
  methods: {
    close () {
      document.body.classList.remove('scroll-lock--modal')
      this.$emit('input', false)
    }
  }
}
</script>

<style lang="scss">
.scroll-lock--modal {
  overflow: hidden;
}
</style>

<style lang="scss" scoped>
@import "@/assets/scss/variables";

.modal {
  position: fixed;
  z-index: 99;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  overflow-x: hidden;
  overflow-y: auto;
  display: flex;
  flex-flow: column nowrap;
  align-items: center;
  justify-content: center;
  background-color: $light-blue;
  box-shadow: 0 0.125rem 2.5rem 0 rgb(0 0 0 / 40%);
}

.modal__close {
  position: absolute;
  z-index: 1000001;
  top: 24px;
  right: 24px;
  width: 1.8rem;
  height: 1.8rem;
  border-radius: 1.25rem;
  box-shadow: 0 0.125rem 1.25rem 0 rgb(0 0 0 / 0%);
  transition-property: box-shadow;
  @include mdTransition;

  @media screen and (min-width: 60rem) {
    right: 32px;
    width: 2.5rem;
    height: 2.5rem;
  }

  svg {
    width: 100%;
    height: 100%;
  }

  circle {
    stroke: $light-blue-600;
    fill: $light-blue-600;
    transition-property: stroke, fill;
    @include mdTransition;
  }

  &:hover,
  &:focus {
    circle {
      stroke: $light-blue-700;
      fill: $light-blue-700;
    }
  }

  &:focus {
    box-shadow: 0 0.125rem 1.25rem 0 rgb(0 0 0 / 40%);

    &:not(:focus-visible) {
      outline: none;
    }
  }
}

.modal__panel {
  position: relative;
  width: 100%;
  display: flex;
  flex-flow: column nowrap;
  padding: 1rem;
  opacity: 1;
  transform: scale(1);
  // The modal does not handle vertical overflow right without this
  overflow: auto;

  @media screen and (min-width: 60rem) {
    padding: 24px;
    // FIXME: temporary solution because popover divs (e.g. date picker) were triggering a scroll bar on desktop
    overflow: unset;
    overflow-y: visible;
  }
}

.modal__container {
  width: 100%;
  max-width: 22rem;
  margin: 0 auto;
}
.modal__panel .modal__content {
  padding: 24px;
}

.modal__title {
  @media screen and (max-width: 60rem) {
    font-size: 1.6875rem;
    margin-bottom: 0.5rem;
  }
}

.modal__footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1rem;
  border-top: 1px solid $light-blue;
  display: flex;
  background-color: white;
  justify-content: center;
}

.modal--md .modal__container {
  max-width: 32rem;
}

.modal--lg .modal__container {
  max-width: 48rem;
}

.modal--xlg .modal__container {
  max-width: 64rem;
}

.modal--full .modal__container {
  max-width: 1170px;
}

.modal--align-top {
  justify-content: flex-start;
  .modal__container {
    padding: 8rem 0.5rem 18rem;
  }
}

.modal-slide-down-enter-active,
.modal-slide-down-leave-active {
  transition-property: opacity, transform;
  @include mdTransition;
}

.modal-slide-down-enter,
.modal-slide-down-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
  transform: scale(0.98);
}

// FIXME: this breaks box-shadow of search bars in modals
// @media (min-width: 1200px) {
//   .modal__panel {
//     padding: 0;
//   }
// }
</style>
