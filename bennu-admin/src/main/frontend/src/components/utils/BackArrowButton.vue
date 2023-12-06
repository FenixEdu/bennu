<template>
  <p
    :class="{ 'h6': type === 'standard' }"
    class="back-arrow-button"
  >
    <!-- FIXME: to === {} does not work bc two objects are never equal through === -->
    <template v-if="!to || to === {}">
      <button
        :class="{ 'back-link-standard': type === 'standard', 'back-link-highlighted': type === 'highlighted' }"
        class="back-link"
        @click.prevent="$emit('back')"
      >
        {{ label }}
      </button>
    </template>
    <router-link
      v-else
      :to="to"
      :class="{ 'back-link-standard': type === 'standard', 'back-link-highlighted': type === 'highlighted' }"
      class="back-link"
    >
      {{ label }}
    </router-link>
  </p>
</template>

<script>
export default {
  name: 'BackArrowButton',
  props: {
    label: {
      type: String,
      required: true
    },
    to: {
      type: Object,
      default: () => {}
    },
    type: {
      type: String,
      default: 'highlighted',
      validator (string) {
        const valid = ['standard', 'highlighted']
        return valid.findIndex(s => s === string) > -1
      }
    }
  }
}
</script>

<style lang="scss" scoped>
  .h6 + h1 {
    margin-top: 0;
  }
  .back-arrow-button {
    margin-bottom: 0.5rem;
    cursor: pointer;
  }
  .back-link-highlighted {
    font-weight: 500;
    color: #009de0;

    &:hover {
      color: #0076dc;
    }

    &::before {
      background-image: url("data:image/svg+xml,%3Csvg width='13' height='10' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M.0228 4.9229a.9567.9567 0 01.2787-.624l.091-.0806a.9644.9644 0 01.0999-.0682l3.8908-3.89a.9576.9576 0 011.3576.0018c.3754.3753.3716.9877.0018 1.3575L2.3622 4.9998l3.3804 3.3808c.3414.3414.3708.8894.0781 1.267l-.0799.0905a.9576.9576 0 01-1.3576.0018l-3.8908-3.89a.9646.9646 0 01-.0998-.0682l-.091-.0805a.9567.9567 0 01-.2788-.624z' fill='%23009DE0' fill-rule='evenodd'/%3E%3C/svg%3E");
    }

    &:hover::before {
      background-image: url("data:image/svg+xml,%3Csvg width='13' height='10' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M.0228 4.9229a.9567.9567 0 01.2787-.624l.091-.0806a.9644.9644 0 01.0999-.0682l3.8908-3.89a.9576.9576 0 011.3576.0018c.3754.3753.3716.9877.0018 1.3575L2.3622 4.9998l3.3804 3.3808c.3414.3414.3708.8894.0781 1.267l-.0799.0905a.9576.9576 0 01-1.3576.0018l-3.8908-3.89a.9646.9646 0 01-.0998-.0682l-.091-.0805a.9567.9567 0 01-.2788-.624z' fill='%230076DC' fill-rule='evenodd'/%3E%3C/svg%3E");
    }
  }
  .back-link-standard {
    &::before {
      background-image: url("data:image/svg+xml,%3Csvg width='13' height='10' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M5.236.285l.033.033c.381.381.38 1 .002 1.379L3.374 3.594h8.26c.531 0 .962.444.962.977v.047c0 .54-.427.976-.962.976H3.31l1.961 1.962a.98.98 0 01-.002 1.379l-.033.033a.976.976 0 01-1.379.002L.284 5.397a.97.97 0 01-.28-.765.97.97 0 01.28-.776L3.857.283a.98.98 0 011.379.002z' fill='%232E3242' fill-rule='evenodd'/%3E%3C/svg%3E");
    }
    &:hover::before {
      background-image: url("data:image/svg+xml,%3Csvg width='13' height='10' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M5.236.285l.033.033c.381.381.38 1 .002 1.379L3.374 3.594h8.26c.531 0 .962.444.962.977v.047c0 .54-.427.976-.962.976H3.31l1.961 1.962a.98.98 0 01-.002 1.379l-.033.033a.976.976 0 01-1.379.002L.284 5.397a.97.97 0 01-.28-.765.97.97 0 01.28-.776L3.857.283a.98.98 0 011.379.002z' fill='%230076DC' fill-rule='evenodd'/%3E%3C/svg%3E");
    }
  }
  .back-link {
    display: inline-flex;
    flex-flow: row nowrap;
    align-items: baseline;
    &::before {
      content: "";
      display: block;
      width: 1em;
      height: 0.75em;
      background-position: center center;
      background-size: 100% auto;
      background-repeat: no-repeat;
    }
  }
</style>
