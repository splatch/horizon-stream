const isVisible = ref(false)

const useModal = () => {
  const openModal = () => (isVisible.value = true)
  const closeModal = () => (isVisible.value = false)

  return { openModal, closeModal, isVisible }
}

export default useModal
