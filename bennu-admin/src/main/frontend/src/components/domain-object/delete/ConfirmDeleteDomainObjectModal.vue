<template>
  <modal
    v-model="isModalOpen"
    class="modal--md"
    :has-close-button="false"
    v-bind="$attrs"
  >
    <template #modal-panel>
      <wizard
        :first-step="firstStep"
        :steps="steps"
      >
        <template #default="{ component, push }">
          <component
            :is="component"
            :domain-object="domainObject"
            @next="push"
            @cancel="cancel"
          />
        </template>
      </wizard>
    </template>
  </modal>
</template>

<script>
import Modal from '@/components/utils/Modal.vue'
import Wizard from '@/components/utils/Wizard.vue'
import ConfirmationStep from './steps/ConfirmationStep.vue'
import SubmitStep from './steps/SubmitStep.vue'

const Steps = Object.freeze({
  CONFIRMATION: 'ConfirmationStep',
  SUBMIT: 'SubmitStep'
})

export default {
  components: {
    Modal,
    Wizard,
    [Steps.CONFIRMATION]: ConfirmationStep,
    [Steps.SUBMIT]: SubmitStep
  },
  props: {
    value: {
      type: Boolean,
      required: true
    },
    domainObject: {
      type: Object,
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
    },
    steps () {
      return [Steps.CONFIRMATION, Steps.SUBMIT]
    },
    firstStep () {
      return this.steps[0]
    }
  },
  methods: {
    cancel () {
      this.isModalOpen = false
    }
  }
}
</script>
