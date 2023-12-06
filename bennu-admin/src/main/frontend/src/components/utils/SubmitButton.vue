<template>
  <button
    class="btn btn--primary btn-submit"
    :class="{ 'btn--success': submitted, 'btn--submitting': submitting }"
    :disabled="disabled"
    :style="{ 'width': width }"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <transition
      name="slide-down"
      mode="out-in"
    >
      <span
        v-show="!(submitting || submitted)"
        class="btn-submit__message btn-submit--default"
      >
        <slot name="submit-text">
          {{ $t('submit') }}
        </slot>
      </span>
    </transition>
    <transition
      name="slide-down"
      mode="out-in"
    >
      <span
        v-show="submitting"
        class="btn-submit__message"
      >
        <svg
          aria-hidden="true"
          viewBox="0 0 18 18"
          xmlns="http://www.w3.org/2000/svg"
        >
          <g
            fill="none"
            fill-rule="evenodd"
          >
            <g class="loading-spinner-wrapper">
              <circle
                class="loading-spinner"
                stroke="currentColor"
                stroke-width="2"
                cx="9"
                cy="9"
                r="7"
              />
            </g>
          </g>
        </svg>
        <slot name="submitting-text">
          <span>{{ $t('submitting') }}</span>
        </slot>
      </span>
    </transition>
    <transition
      name="slide-down"
      mode="out-in"
    >
      <span
        v-show="submitted"
        class="btn-submit__message btn-submit--submitted"
      >
        <svg
          aria-hidden="true"
          viewBox="0 0 18 18"
          xmlns="http://www.w3.org/2000/svg"
        >
          <g
            fill="none"
            fill-rule="evenodd"
          >
            <path
              class="success-checkmark"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M1 10.474L6.222 16 17 2"
            />
          </g>
        </svg>
        <slot name="submitted-text">
          <span>{{ $t('submitted') }}</span>
        </slot>
      </span>
    </transition>
  </button>
</template>
<script>
// TODO: use live areas for announcing state change (a11y)
export default {
  props: {
    submitting: {
      type: Boolean,
      required: true
    },
    submitted: {
      type: Boolean,
      required: true
    },
    disabled: {
      type: Boolean,
      default: false
    },
    width: {
      type: String,
      default: '12rem'
    }
  },
  i18n: {
    messages: {
      pt: {
        submit: 'Submeter',
        submitting: 'A guardar alterações...',
        submitted: 'Alterações guardadas'
      },
      en: {
        submit: 'Submit',
        submitting: 'Saving changes...',
        submitted: 'Saved changes'
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.btn-submit {
  overflow: hidden;
}

.btn--submitting {
  opacity: 1 !important;
}

.btn-submit__message {
  display: flex;
  align-items: center;

  svg {
    width: 18px;
    height: 18px;
    margin-right: 0.5rem;

    .loading-spinner-wrapper {
      transform-origin: center;
      opacity: 1;
    }

    .loading-spinner {
      transform-origin: center;
      transition: all 0.6s cubic-bezier(0.68, -0.6, 0.32, 1.6), stroke 0;
      stroke-linecap: round;
      stroke-dasharray: 42;
      stroke-dashoffset: 40;
      animation: circle-animation 1.5s ease-in-out infinite;
    }

    .success-checkmark {
      stroke-dasharray: 39px;
    }
  }
}

.slide-down-enter-active {
  transition: all 0.6s cubic-bezier(0.76, 0, 0.24, 1);
}

.slide-down-enter-to {
  opacity: 1;
}

.slide-down-leave {
  width: 0;
  opacity: 0;
}

.slide-down-enter,
.slide-down-leave-to {
  transform: translateY(-50%);
  opacity: 0;
}

@keyframes circle-animation {
  0% {
    stroke-dashoffset: 42;
    transform: rotate(0);
  }

  60% {
    stroke-dashoffset: 15;
    transform: rotate(45deg);
  }

  100% {
    stroke-dashoffset: 42;
    transform: rotate(360deg);
  }
}
</style>
