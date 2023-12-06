<template>
  <div
    :class="{ 'dropdown--dropup': dropup }"
    class="dropdown"
  >
    <div
      class="dropdown__trigger-container"
      @mousedown.prevent="!disabled && open()"
      @touchstart.prevent="!disabled && open()"
      @click.prevent="!disabled && open()"
    >
      <slot
        name="dropdown-trigger"
        :expanded="expanded"
        :attrs="{
          'disabled': disabled,
          'aria-haspopup': 'true',
          'aria-expanded': String(expanded)
        }"
      />
    </div>
    <div
      :id="id"
      tabindex="-1"
      :class="[`dropdown--${size}`, { 'dropdown__panel--hidden': !expanded }]"
      class="dropdown__panel"
      @click="resolvePanelClick"
    >
      <slot
        name="dropdown-panel"
        :expanded="expanded"
      />
    </div>
  </div>
</template>

<script>
import { createFocusTrap } from 'focus-trap'

export default {
  name: 'Dropdown',
  props: {
    size: {
      type: String,
      default: 'md'
    },
    dropup: {
      type: Boolean,
      default: false
    },
    closesOnClick: {
      type: Boolean,
      default: true
    },
    value: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      expanded: false,
      id: `dropdown-${Math.floor(Math.random() * 10000)}`,
      trap: undefined
    }
  },
  watch: {
    value (newVal) {
      this.expanded = newVal
    }
  },
  mounted () {
    this.trap = createFocusTrap(`#${this.id}`, {
      escapeDeactivates: true,
      clickOutsideDeactivates: true,
      returnFocusOnDeactivate: true,
      initialFocus: `#${this.id}`,
      onDeactivate: () => {
        this.expanded = false
        this.$emit('input', false)
      }
    })
  },
  beforeDestroy () {
    this.trap.deactivate()
  },
  methods: {
    open () {
      this.expanded = true
      this.$emit('input', true)
      setTimeout(() => {
        this.trap.activate()
      }, 100)
    },
    close () {
      this.trap.deactivate()
    },
    resolvePanelClick () {
      if (this.closesOnClick) {
        this.close()
      }
    }
  }
}
</script>

<style lang="scss">
@use "sass:color";
@import "@/assets/scss/variables";

.dropdown {
  position: relative;
}

.dropdown--xs {
  min-width: 6.25rem;
}

.dropdown--md {
  min-width: 11.5rem;
}

.dropdown--lg {
  min-width: 18.125rem;
}

.dropdown__panel {
  position: absolute;
  top: 1.5em;
  top: calc(100% + 7px);
  right: -0.5rem;
  word-wrap: none;
  white-space: nowrap;
  z-index: 10000;
  opacity: 1;
  transform: translateY(0);
  border-radius: 2px;
  background: #fff;
  box-shadow: 0 0 3px 0 rgb(0 0 0 / 10%), 0 5px 10px 0 rgb(0 0 0 / 15%);
  transition: all 0.3s ease;
  visibility: visible;

  &:focus {
    outline: none;
  }
}

.dropdown__panel--hidden {
  opacity: 0;
  transform: translateY(-0.5rem);
  visibility: hidden;
}

.dropdown__panel::before {
  position: absolute;
  content: "";
  top: -0.5rem;
  right: 1rem;
  filter: drop-shadow(0 -2px 1px rgb(0 0 0 / 5%));
  width: 0;
  height: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-bottom: 8px solid #fff;
}
.dropdown--dropup {
  .dropdown__panel {
    top: unset;
    bottom: calc(100% + 14px);
    &::before {
      top: unset;
      bottom: -0.5rem;
      filter: drop-shadow(0 2px 1px rgb(0 0 0 / 5%));
      border-bottom: none;
      border-top: 8px solid #fff;
    }
  }
}

.dropdown__search {
  .f-search {
    margin: 0;
    border-bottom: 1px solid #eef2f5;
  }
}

// Global dropdown menu styles

.dropdown-user__details {
  padding: 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eef2f5;
}

.dropdown-user__text {
  margin-left: 16px;
}

.dropdown-user__text .name {
  font-weight: 600;
  max-width: 12rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-user__text .user-name {
  font-size: 16px;
  color: #717782;
}

.dropdown-menu {
  display: flex;
  flex-direction: column;
  position: relative;
}

.dropdown-menu__item ~ .dropdown-menu__item a,
.dropdown-menu__item ~ .dropdown-menu__item button,
.dropdown-menu__item ~ .dropdown-menu__item .dropdown-menu__section-heading {
  border-top: 1px solid #eef2f5;
}

.dropdown-menu__section-heading {
  font-weight: 600;
  padding: 1rem 1rem 0.5rem;
}
.dropdown-menu .link-icon {
  // same with as user-avatar
  width: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

// review main-nav list item and anchor styles
.main-nav .dropdown-menu__link,
.dropdown-menu__link {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 1rem;
  color: #45555f;
  transition-property: background-color;
  @include mdTransition;

  &:hover,
  &:focus {
    background-color: #eef2f5;
    color: #45555f;
  }

  &:active {
    background-color: color.adjust(#eef2f5, $lightness: -2%);
  }

  &[disabled] {
    opacity: 0.5;
  }
}

@mixin checkmark-before {
  content: "";
  display: block;
  position: absolute;
  background-image: url("data:image/svg+xml,%3Csvg width='24' height='24' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cpath fill='none' d='M0 0h24v24H0z'/%3E%3Cpath stroke='%23009DE0' stroke-width='3' stroke-linecap='round' stroke-linejoin='round' d='M7 13.2l3.8 3.8 7.7-9.5'/%3E%3C/g%3E%3C/svg%3E");
  background-size: 100% 100%;
  background-repeat: no-repeat;
}
.dropdown--with-check {
  .dropdown-menu__link {
    padding-left: 3rem;
  }
  .dropdown-menu__link--selected {
    position: relative;
    &::before {
      @include checkmark-before();

      left: 0.75rem;
      width: 1.5rem;
      height: 1.5rem;
      top: 0.75rem;
    }
  }
}

.dropdown__trigger-container {
  & > :focus:not(:focus-visible) {
    outline: none;
  }
}

.dropdown--menubar {
  .dropdown-menu {
    padding: 1.5rem 0 1rem;
  }

  .dropdown-menu__label {
    padding-right: 1.5rem;
    margin-bottom: 0.75rem;
    padding-left: 2.5rem;
    color: $gray-400;
  }
  .dropdown-menu__item .dropdown-menu__link {
    padding: 0.5rem 1.5rem 0.5rem 2.75rem;
    position: relative;

    &.dropdown-menu__link--selected::before {
      @include checkmark-before();

      top: calc(50% - 0.75rem);
      left: 0.75rem;
      height: 1.5rem;
      width: 1.5rem;
    }
  }
  .dropdown-menu__item + .dropdown-menu__item .dropdown-menu__link {
    border-top: none;
  }
}
</style>
