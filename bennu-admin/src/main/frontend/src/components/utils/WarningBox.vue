<template>
  <div
    class="warning-box"
    :class="{
      'warning-box--inline': inline,
      [`warning-box--${type}`]: true
    }"
    v-bind="$attrs"
  >
    <div class="warning-box warning-box__container">
      <div
        class="label warning-box__badge"
        :class="`label--${type}`"
      >
        <slot name="badge" />
      </div>
      <div :class="`u-text-${type}`">
        <slot name="message" />
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    type: {
      type: String,
      default: 'danger',
      validator: prop => ['danger', 'warning'].includes(prop)
    },
    inline: {
      type: Boolean,
      default: false
    }
  }
}
</script>
<style lang="scss" scoped>
@use "sass:color";
@import "@/assets/scss/variables";

.warning-box__container {
  border-radius: 0.25rem;
  padding: 1.5rem;
}
.warning-box__badge {
  margin-bottom: 1rem;
}

.warning-box--danger {
  .warning-box__container {
    background-color: #fbeaec;
    border: 2px solid color.adjust($magenta, $alpha: -0.5);
  }
  .warning-box__badge {
    width: max-content;
  }
}

.warning-box--warning {
  .warning-box__container {
    background-color: #fbeaec;
    border: 2px solid color.adjust($orange, $alpha: -0.5);
  }
  .warning-box__badge {
    width: max-content;
  }
}
.warning-box--inline {
  .warning-box__container {
    padding: 1.5rem;
  }

  .warning-box__badge {
    margin-bottom: 0.5rem;
  }

  @media screen and (min-width: 60rem) {
    .warning-box__container {
      padding: 1rem 1.5rem;
      display: flex;
      flex-flow: row nowrap;
      align-items: center;
    }

    .warning-box__badge {
      display: inline;
      margin-right: 0.5rem;
      margin-bottom: 0;
    }
  }
}
</style>
