<template>
  <wizard
    class="page"
    :steps="steps"
    :first-step="firstStep"
  >
    <template #default="{component, push, back}">
      <component
        :is="component"
        :domain-object="domainObject"
        :domain-object-form="domainObjectForm"
        :form-data.sync="formData"
        @previous="back"
        @next="push"
        @touch="dirty = $event"
      />
      <prevent-leave-modal
        v-bind="preventLeave__attrs"
        v-on="preventLeave__listeners"
      />
    </template>
  </wizard>
</template>

<script>
import Wizard from '@/components/utils/Wizard.vue'
import EditFormStep from '@/components/domain-object/edit/EditFormStep.vue'
import ReviewAndSubmitStep from '@/components/domain-object/edit/ReviewAndSubmitStep.vue'
import NativePreventLeave from '@/mixins/NativePreventLeave'
import PreventLeave from '@/mixins/PreventLeave'

const Steps = Object.freeze({
  EDIT_FORM: 'EditFormStep',
  REVIEW_AND_SUBMIT: 'ReviewAndSubmitStep'
})

export default {
  components: {
    Wizard,
    [Steps.EDIT_FORM]: EditFormStep,
    [Steps.REVIEW_AND_SUBMIT]: ReviewAndSubmitStep
  },
  mixins: [NativePreventLeave, PreventLeave],
  props: {
    domainObject: {
      type: Object,
      required: true
    },
    domainObjectForm: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      formData: this.domainObjectForm.data ?? {}
    }
  },
  computed: {
    steps () {
      return [Steps.EDIT_FORM, Steps.REVIEW_AND_SUBMIT]
    },
    firstStep () {
      return this.steps[0]
    }
  }
}
</script>
