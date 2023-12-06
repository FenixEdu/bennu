<template>
  <file-upload
    v-model="$v.$model"
    :required="field.required"
    :name="$attrs.name || field.field"
    :file-types="field.allowedFileTypes"
    :label="translate(field.label)"
    :description="translate(field.description)"
    :invalid="$v.$error"
  >
    <template
      v-if="field.templateUrl"
      #additional-info
    >
      <a
        :href="field.templateUrl"
        class="btn--link"
      >
        {{ $t('dynamic-form.fields.file.template') }}
      </a>
    </template>
    <template
      v-if="field.tooltip"
      #tooltip
    >
      <p>{{ translate(field.tooltip) }}</p>
    </template>
    <template #error-message>
      <template v-if="$v.$params.required && !$v.required">
        {{ $t('dynamic-form.fields.file.errors.required') }}
      </template>
      <template v-if="$v.$params.fileSize && !$v.fileSize">
        {{ $t('dynamic-form.fields.file.errors.file-size', { maxSizeMB: field.maxSizeMB }) }}
      </template>
    </template>
  </file-upload>
</template>

<script>
import requiredIf from 'vuelidate/lib/validators/requiredIf'
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import FileUpload from '@/components/utils/fields/FileUpload.vue'
import { withParams, req } from 'vuelidate/lib/validators/common'
import { DynamicFormField } from '@/components/dynamic-form/utils/fields'
import client from '@/api/client'

const fileSize = maxBytes => withParams(
  { type: 'fileSize', maxBytes },
  file => {
    return !req(file) || (file?.size ?? 0) <= maxBytes
  }
)

export const extend = superclass => class extends superclass {
  get validations () {
    const validations = { }
    if (this.field.required) {
      validations.required = requiredIf(() => this.exists())
    }
    if (this.field.maxSizeMB !== undefined) {
      validations.fileSize = fileSize(Math.floor(this.field.maxSizeMB * 1024 * 1024))
    }
    return validations
  }

  get emptyValue () {
    return null
  }

  parseSubmitValue () {
    if (!this.previousSubmitValue) {
      return this.emptyValue
    }
    // it's an empty File on purpose:
    // we have the downloadUrl of the submitted file
    // but this function can't be async, so we can't fetch the bytes to create an actual File
    // fetch the actual file content on getSubmitBlobs, when submitting to the server
    return new File([], this.previousSubmitValue.fileName, { type: this.previousSubmitValue.fileType })
  }

  async getSubmitBlobs () {
    if (!this.exists() || this.value === this.emptyValue) {
      return {}
    } else if (this.value instanceof File && this.value.size !== 0) {
      return { '': this.value }
    } else if (this.previousSubmitValue) {
      const response = await client.get(this.previousSubmitValue.downloadUrl, { responseType: 'blob' })
      const file = new File([response.data], this.previousSubmitValue.fileName, { type: this.previousSubmitValue.fileType })
      return { '': file }
    } else {
      return {}
    }
  }

  async getSubmitValue () {
    // File is sent through getSubmitBlobs, and added to the JSON data on the server
    return undefined
  }

  takeSnapshot () {
    const { snapshot, destroy } = super.takeSnapshot()
    if (this.value === this.emptyValue) {
      return { snapshot, destroy }
    } else if (this.value instanceof File && this.value.size !== 0) {
      // the user uploaded a new file
      const objectUrl = URL.createObjectURL(this.value)
      return {
        snapshot: {
          ...snapshot,
          value: {
            downloadUrl: objectUrl
          }
        },
        async destroy () {
          await destroy()
          URL.revokeObjectURL(objectUrl)
        }
      }
    } else {
      // there's a File object, but it has size zero (it's the placeholder File we creted in parseSubmitValue)
      return {
        snapshot: { ...snapshot, value: { downloadUrl: this.previousSubmitValue.downloadUrl } },
        destroy
      }
    }
  }
}

export const FormField = extend(DynamicFormField)

export default {
  name: 'FileField',
  components: {
    FileUpload
  },
  mixins: [
    TranslateApiStringMixin
  ],
  props: {
    formField: {
      type: DynamicFormField,
      required: true
    }
  },
  computed: {
    field () {
      return this.formField.field
    },
    $v () {
      return this.formField.$v
    }
  }
}
</script>
