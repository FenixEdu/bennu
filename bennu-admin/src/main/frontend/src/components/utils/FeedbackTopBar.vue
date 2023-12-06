<template>
  <div
    v-if="topMessage.active"
    role="alert"
    :class="{ 'feedback-top-bar--informative': topMessage.type === 'info', 'feedback-top-bar--warning': topMessage.type === 'warn', 'feedback-top-bar--danger': topMessage.type === 'danger'}"
    class="feedback-top-bar"
  >
    <div class="feedback-top-bar-container container">
      <div class="feedback-top-bar__message">
        <p>{{ topMessage.msg[$i18n.locale] }}</p>
      </div>
      <button
        v-if="topMessage.dismiss"
        class="feedback-top-bar__close icon icon-close"
        :aria-label="$t('dismiss')"
        @click.prevent="dismissMessage()"
      >
        <svg
          width="16"
          height="16"
          viewBox="0 0 16 16"
          xmlns="http://www.w3.org/2000/svg"
        >
          <g
            fill="#FFF"
            stroke="#FFF"
            stroke-width=".75"
            fill-rule="evenodd"
          >
            <path d="M1.32300768 14.47519407c-.30265632-.30265633-.29701958-.79899593.00038066-1.09639618l12.05540931-12.0554093c.30286656-.30286657.79162976-.30514703 1.09639615-.00038064.30265632.30265632.29701956.79899594-.00038064 1.09639615L2.41940386 14.4748134c-.30286657.30286656-.7916298.30514705-1.09639618.00038067z" />
            <path d="M1.17390693 1.17390693c.30265633-.30265632.79899593-.29701958 1.09639618.00038067l12.05540932 12.05540932c.30286656.30286656.30514702.79162976.00038064 1.09639615-.30265633.30265632-.79899595.29701956-1.09639615-.00038064L1.1742876 2.2703031c-.30286656-.30286656-.30514705-.79162979-.00038067-1.09639618z" />
          </g>
        </svg>
      </button>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'FeedbackTopBar',
  computed: {
    ...mapState(['topMessage'])
  },
  methods: {
    dismissMessage () {
      this.$notification.clear()
    }
  },
  i18n: {
    messages: {
      pt: {
        dismiss: 'Fechar este alerta'
      },
      en: {
        dismiss: 'Dismiss this alert'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/variables";
.feedback-top-bar {
  position: fixed;
  top: $header-height;
  z-index: 1; // FIXME: this was previously 9999. it was rendering on top of dropdowns in TopNavBar, which had z-index: 10000
  width: 100%;
  min-height: 44px;
  padding: 0.6rem 0 0.5rem;

  // default state
  background: $dark;
  color: $light-blue-700;

  // opacity: 0;
  // visibility: hidden;
  // pointer-events: none;

  transition: opacity 0.6s cubic-bezier(0.77, 0, 0.175, 1), visibility 0.2s cubic-bezier(0.77, 0, 0.175, 1);

  &.is-visible {
    opacity: 1;
    visibility: visible;
    pointer-events: all;
  }

  .icon-close {
    opacity: 0.9;
  }

  .icon-close:hover .icon-stroke,
  .icon-close:focus .icon-stroke {
    fill: #fff;
    stroke: $dark;
  }
}

// Other types of feedback: Informative
.feedback-top-bar--informative {
  background: $blue-600;
  color: #fff;

  .icon-close:hover,
  .icon-close:focus {
    opacity: 1;
  }
}
// Other types of feedback: Danger
.feedback-top-bar--danger {
  background: $magenta-600;
  color: #fff;

  .icon-close:hover,
  .icon-close:focus {
    opacity: 1;
  }
}

// Other types of feedback: Warning
.feedback-top-bar--warning {
  background: $orange;
  color: #fff;

  .icon-close:hover,
  .icon-close:focus {
    opacity: 1;
  }
}

.feedback-top-bar p {
  color: #fff;
}

.icon-close .icon-stroke {
  transition: all 0.2s ease-in-out;
}

.feedback-top-bar-container {
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
}

.feedback-top-bar__message {
  flex-grow: 1;
}

.page-with-card .feedback-top-bar,
.page-with-footer .feedback-top-bar {
  top: 0;
}
</style>
