import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  Alert,
  CircularProgress,
  FormHelperText,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { Person as PersonIcon, Business as BusinessIcon, LocationOn as LocationIcon } from '@mui/icons-material';
import { apiService } from '../services/apiService';
import { ProviderCreate, Provider } from '../types';

const StyledDialog = styled(Dialog)(({ theme }) => ({
  '& .MuiDialog-paper': {
    borderRadius: theme.spacing(2),
    padding: theme.spacing(1),
    maxWidth: '500px',
    width: '100%',
  },
}));

const FormField = styled(Box)(({ theme }) => ({
  display: 'flex',
  alignItems: 'flex-end',
  marginBottom: theme.spacing(2),
  '& .MuiSvgIcon-root': {
    marginRight: theme.spacing(1),
    marginBottom: theme.spacing(1),
    color: theme.palette.text.secondary,
  },
}));

interface ProviderFormProps {
  open: boolean;
  onClose: () => void;
  onSuccess: (provider: Provider) => void;
}

interface FormData {
  nombre: string;
  razonSocial: string;
  direccion: string;
}

interface FormErrors {
  nombre?: string;
  razonSocial?: string;
  direccion?: string;
}

const ProviderForm: React.FC<ProviderFormProps> = ({ open, onClose, onSuccess }) => {
  const [formData, setFormData] = useState<FormData>({
    nombre: '',
    razonSocial: '',
    direccion: '',
  });
  
  const [errors, setErrors] = useState<FormErrors>({});
  const [loading, setLoading] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  // Validaciones del formulario
  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    // Validar nombre (requerido, mínimo 3 caracteres)
    if (!formData.nombre.trim()) {
      newErrors.nombre = 'El nombre es requerido';
    } else if (formData.nombre.trim().length < 3) {
      newErrors.nombre = 'El nombre debe tener al menos 3 caracteres';
    }

    // Validar razón social (opcional, pero si se proporciona mínimo 3 caracteres)
    if (formData.razonSocial.trim() && formData.razonSocial.trim().length < 3) {
      newErrors.razonSocial = 'La razón social debe tener al menos 3 caracteres';
    }

    // Validar dirección (opcional, pero si se proporciona mínimo 5 caracteres)
    if (formData.direccion.trim() && formData.direccion.trim().length < 5) {
      newErrors.direccion = 'La dirección debe tener al menos 5 caracteres';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (field: keyof FormData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const value = event.target.value;
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Limpiar error específico cuando el usuario empiece a escribir
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
    
    // Limpiar error general
    if (submitError) {
      setSubmitError(null);
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setSubmitError(null);

    try {
      // Preparar datos para envío (eliminar campos vacíos opcionales)
      const providerData: ProviderCreate = {
        nombre: formData.nombre.trim(),
      };

      if (formData.razonSocial.trim()) {
        providerData.razonSocial = formData.razonSocial.trim();
      }

      if (formData.direccion.trim()) {
        providerData.direccion = formData.direccion.trim();
      }

      const newProvider = await apiService.createProvider(providerData);
      
      // Limpiar formulario
      setFormData({ nombre: '', razonSocial: '', direccion: '' });
      setErrors({});
      
      // Notificar éxito y cerrar
      onSuccess(newProvider);
      onClose();
      
    } catch (err) {
      console.error('Error creating provider:', err);
      
      if (err instanceof Error) {
        // Manejar errores específicos de la API
        if (err.message.includes('CONFLICT')) {
          setSubmitError('Ya existe un proveedor con ese nombre');
        } else if (err.message.includes('VALIDATION_ERROR')) {
          setSubmitError('Error de validación en los datos proporcionados');
        } else {
          setSubmitError('Error al crear el proveedor. Intenta nuevamente.');
        }
      } else {
        setSubmitError('Error de conexión con el servidor');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    if (!loading) {
      // Limpiar formulario al cerrar
      setFormData({ nombre: '', razonSocial: '', direccion: '' });
      setErrors({});
      setSubmitError(null);
      onClose();
    }
  };

  return (
    <StyledDialog 
      open={open} 
      onClose={handleClose}
      aria-labelledby="provider-form-title"
    >
      <DialogTitle id="provider-form-title">
        Agregar Nuevo Proveedor
      </DialogTitle>
      
      <form onSubmit={handleSubmit}>
        <DialogContent>
          {submitError && (
            <Alert severity="error" sx={{ mb: 2, borderRadius: 1 }}>
              {submitError}
            </Alert>
          )}

          {/* Campo Nombre */}
          <FormField>
            <PersonIcon />
            <TextField
              fullWidth
              label="Nombre del Proveedor"
              value={formData.nombre}
              onChange={handleInputChange('nombre')}
              error={!!errors.nombre}
              helperText={errors.nombre}
              disabled={loading}
              required
              variant="standard"
              placeholder="Ej: Juan Pérez"
            />
          </FormField>

          {/* Campo Razón Social */}
          <FormField>
            <BusinessIcon />
            <TextField
              fullWidth
              label="Razón Social"
              value={formData.razonSocial}
              onChange={handleInputChange('razonSocial')}
              error={!!errors.razonSocial}
              helperText={errors.razonSocial || 'Opcional'}
              disabled={loading}
              variant="standard"
              placeholder="Ej: Empresa S.A. de C.V."
            />
          </FormField>

          {/* Campo Dirección */}
          <FormField>
            <LocationIcon />
            <TextField
              fullWidth
              label="Dirección"
              value={formData.direccion}
              onChange={handleInputChange('direccion')}
              error={!!errors.direccion}
              helperText={errors.direccion || 'Opcional'}
              disabled={loading}
              variant="standard"
              placeholder="Ej: Calle Principal #123, Ciudad"
              multiline
              maxRows={2}
            />
          </FormField>

          <FormHelperText sx={{ mt: 1 }}>
            * Los campos marcados son obligatorios
          </FormHelperText>
        </DialogContent>

        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button 
            onClick={handleClose} 
            disabled={loading}
            color="inherit"
          >
            Cancelar
          </Button>
          
          <Button
            type="submit"
            variant="contained"
            disabled={loading || !formData.nombre.trim()}
            startIcon={loading ? <CircularProgress size={16} /> : null}
          >
            {loading ? 'Creando...' : 'Crear Proveedor'}
          </Button>
        </DialogActions>
      </form>
    </StyledDialog>
  );
};

export default ProviderForm;