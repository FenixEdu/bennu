<template>
  <modal
    v-model="isModalOpen"
    class="modal--md"
    prevent-close
    :has-close-button="false"
  >
    <template #modal-panel>
      <card-layout-wrapper
        type="modal"
        layout="centered"
      >
        <template #icon>
          <icon
            icon-color="var(--magenta)"
            name="circle-exclamation"
          />
        </template>
        <slot
          slot="title"
          name="title"
        />
        <slot
          slot="description"
          name="description"
        />
        <template #card-content>
          <slot
            name="errors"
          />
          <slot
            name="actions"
          />
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
