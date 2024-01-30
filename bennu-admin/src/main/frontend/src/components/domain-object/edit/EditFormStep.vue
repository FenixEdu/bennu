<template>
  <div>
    <header class="section-header">
      <div class="section-header__text">
        <p class="h6 section-header__parent-page">
          <router-link :to="{ name: 'DomainObjectPage', params: { objectId: domainObject.objectId } }">
            <span
              class="i-back-arrow"
              :aria-label="$t('actions.aria.back-to')"
            />
            <span>{{ $t('actions.back') }}</span>
          </router-link>
        </p>
        <h1 class="h1 section-header__title">
          {{ $t('header', { object: domainObject.objectId }) }}
        </h1>
        <p>{{ $t('description') }}</p>
      </div>
    </header>
    <dynamic-form
      :dynamic-form="form"
      @previous="$emit('previous', $event)"
      @next="next"
      @touch="onFormTouch"
    />
  </div>
</template>

<script>
import DynamicFormComponent from '@/components/dynamic-form/DynamicForm.vue'
import TranslateApiString from '@/mixins/TranslateApiString'
import { DynamicForm } from '@/components/dynamic-form/utils/form'

export default {
  components: {
    DynamicForm: DynamicFormComponent
  },
  mixins: [TranslateApiString],
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
      form: new DynamicForm(this.domainObjectForm.form, this.domainObjectForm.data)
    }
  },
  methods: {
    onFormTouch (touched) {
      this.$emit('touch', touched)
    },
    async next ($event) {
      const data = await this.form.getSubmitData()
      this.$emit('update:formData', data)
      this.$emit('next', $event)
    }
  },
  i18n: {
    messages: {
      pt: {
        header: 'Editar objeto de domínio "{object}"',
        description: 'Edite o objeto de domínio'
      },
      en: {
        header: 'Edit domain object "{object}"',
        description: 'Edit the domain object'
      }
    }
  }
}
</script>
