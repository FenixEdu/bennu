<template>
  <modal
    v-model="isModalOpen"
    class="modal--md"
    :has-close-button="false"
    initial-focus="#prevent-leave-modal__title"
    aria-labelledby="prevent-leave-modal__title"
  >
    <template #modal-panel>
      <card-layout-wrapper
        type="modal"
        layout="centered"
      >
        <slot
          slot="icon"
          name="icon"
        >
          <icon
            name="circle-exclamation"
            icon-color="var(--blue)"
          />
        </slot>
        <template #title>
          <span
            id="prevent-leave-modal__title"
            tabindex="-1"
            class="u-a11y-focusable"
          >
            <slot name="title-text">
              {{ $t('header') }}
            </slot>
          </span>
        </template>
        <slot
          slot="description"
          name="description"
        />
        <template #card-content>
          <div class="btn-group btn-group--vertical">
            <button
              type="button"
              class="btn--primary btn--outline btn--full"
              @click.prevent="$listeners.leave"
            >
              <slot name="leave-text">
                {{ $t('actions.leave') }}
              </slot>
            </button>
            <button
              type="button"
              class="btn--primary btn--full"
              @click.prevent="isModalOpen = false"
            >
              <slot name="prevent-text">
                {{ $t('actions.prevent') }}
              </slot>
            </button>
          </div>
        </template>
      </card-layout-wrapper>
    </template>
  </modal>
</template>

<script>
import Modal from '@/components/utils/Modal.vue'
import CardLayoutWrapper from '@/components/CardLayoutWrapper.vue'
import Icon from '@/components/utils/Icon.vue'

export default {
  components: {
    Modal,
    CardLayoutWrapper,
    Icon
  },
  i18n: {
    messages: {
      pt: {
        header: 'Existem alterações por guardar. Deseja abandonar a página sem guardar?',
        actions: {
          leave: 'Abandonar a página sem guardar',
          prevent: 'Cancelar e permanecer na página'
        }
      },
      en: {
        header: 'There are unsaved changes. Do you want to leave this page without saving?',
        actions: {
          leave: 'Leave the page without saving changes',
          prevent: 'Cancel and stay on this page'
        }
      }
    }
  },
  props: {
    value: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    isModalOpen: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('input', val)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.btn-group {
  margin-top: 3.5rem;
}
</style>
