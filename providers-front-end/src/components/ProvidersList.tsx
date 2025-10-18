import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  CircularProgress,
  Alert,
  IconButton,
  Chip,
  TablePagination,
  Card,
  CardContent,
  CardActions,
  Grid,
  Fab,
  Tooltip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Button,
} from '@mui/material';
import {
  Delete as DeleteIcon,
  Add as AddIcon,
  Business as BusinessIcon,
  LocationOn as LocationIcon,
  Person as PersonIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import { apiService } from '../services/apiService';
import { Provider, ProvidersListResponse, PaginationParams } from '../types';

const ProvidersContainer = styled(Container)(({ theme }) => ({
  paddingTop: theme.spacing(3),
  paddingBottom: theme.spacing(3),
}));

const HeaderSection = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: theme.spacing(3),
}));

const ProviderCard = styled(Card)(({ theme }) => ({
  marginBottom: theme.spacing(2),
  borderRadius: theme.spacing(2),
  boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
  transition: 'all 0.3s ease',
  '&:hover': {
    transform: 'translateY(-2px)',
    boxShadow: '0 4px 16px rgba(0,0,0,0.15)',
  },
}));

const VirtualListContainer = styled(Box)(({ theme }) => ({
  height: '500px',
  border: `1px solid ${theme.palette.divider}`,
  borderRadius: theme.spacing(1),
  overflow: 'hidden',
}));

const AddButton = styled(Fab)(({ theme }) => ({
  position: 'fixed',
  bottom: theme.spacing(3),
  right: theme.spacing(3),
  backgroundColor: theme.palette.primary.main,
  '&:hover': {
    backgroundColor: theme.palette.primary.dark,
  },
}));

interface ProviderItemProps {
  provider: Provider;
  onDelete: (id: string, nombre: string) => void;
}

const ProviderItem: React.FC<ProviderItemProps> = ({ provider, onDelete }) => {
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <ProviderCard>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="flex-start">
          <Box flex={1}>
            <Typography variant="h6" component="h3" gutterBottom>
              <PersonIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
              {provider.nombre}
            </Typography>
            
            {provider.razonSocial && (
              <Typography variant="body2" color="text.secondary" gutterBottom>
                <BusinessIcon sx={{ mr: 1, verticalAlign: 'middle', fontSize: 16 }} />
                {provider.razonSocial}
              </Typography>
            )}
            
            {provider.direccion && (
              <Typography variant="body2" color="text.secondary" gutterBottom>
                <LocationIcon sx={{ mr: 1, verticalAlign: 'middle', fontSize: 16 }} />
                {provider.direccion}
              </Typography>
            )}
            
            <Chip 
              label={`Creado: ${formatDate(provider.createdAt)}`}
              size="small"
              variant="outlined"
              color="primary"
              sx={{ mt: 1 }}
            />
          </Box>
          
          <CardActions>
            <Tooltip title="Eliminar proveedor">
              <IconButton 
                onClick={() => onDelete(provider.id, provider.nombre)}
                color="error"
                size="small"
              >
                <DeleteIcon />
              </IconButton>
            </Tooltip>
          </CardActions>
        </Box>
      </CardContent>
    </ProviderCard>
  );
};


interface ProvidersListProps {
  onAddProvider: () => void;
  refreshTrigger?: number;
}

const ProvidersList: React.FC<ProvidersListProps> = ({ onAddProvider, refreshTrigger }) => {
  const [providers, setProviders] = useState<Provider[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [pagination, setPagination] = useState<PaginationParams>({ page: 0, size: 10 });
  const [total, setTotal] = useState(0);
  const [deleteDialog, setDeleteDialog] = useState<{
    open: boolean;
    providerId: string;
    providerName: string;
  }>({ open: false, providerId: '', providerName: '' });

  const fetchProviders = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const response: ProvidersListResponse = await apiService.getProviders(pagination);
      setProviders(response.items);
      setTotal(response.total);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar proveedores');
      console.error('Error fetching providers:', err);
    } finally {
      setLoading(false);
    }
  }, [pagination]);

  useEffect(() => {
    fetchProviders();
  }, [fetchProviders, refreshTrigger]);

  const handlePageChange = (event: unknown, newPage: number) => {
    setPagination(prev => ({ ...prev, page: newPage }));
  };

  const handleRowsPerPageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPagination({
      page: 0,
      size: parseInt(event.target.value, 10),
    });
  };

  const handleDeleteClick = (id: string, nombre: string) => {
    setDeleteDialog({
      open: true,
      providerId: id,
      providerName: nombre,
    });
  };

  const handleDeleteConfirm = async () => {
    try {
      await apiService.deleteProvider(deleteDialog.providerId);
      await fetchProviders(); // Refrescar la lista
      setDeleteDialog({ open: false, providerId: '', providerName: '' });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al eliminar proveedor');
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialog({ open: false, providerId: '', providerName: '' });
  };

  return (
    <ProvidersContainer maxWidth="lg">
      <HeaderSection>
        <Typography variant="h4" component="h1" fontWeight={700}>
          Gestión de Proveedores
        </Typography>
        
        <Tooltip title="Actualizar lista">
          <IconButton onClick={fetchProviders} color="primary">
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </HeaderSection>

      {error && (
        <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 2, borderRadius: 2 }}>
        <Typography variant="h6" gutterBottom>
          Proveedores ({total} total)
        </Typography>

        {loading ? (
          <Box display="flex" justifyContent="center" py={4}>
            <CircularProgress />
          </Box>
        ) : providers.length === 0 ? (
          <Box textAlign="center" py={4}>
            <Typography variant="body1" color="text.secondary">
              No hay proveedores registrados
            </Typography>
          </Box>
        ) : (
          <>
            {/* Lista normal de proveedores */}
            <Box sx={{ maxHeight: '500px', overflow: 'auto' }}>
              {providers.map((provider) => (
                <Box key={provider.id} px={2} py={1}>
                  <ProviderItem provider={provider} onDelete={handleDeleteClick} />
                </Box>
              ))}
            </Box>

            <TablePagination
              component="div"
              count={total}
              page={pagination.page}
              onPageChange={handlePageChange}
              rowsPerPage={pagination.size}
              onRowsPerPageChange={handleRowsPerPageChange}
              rowsPerPageOptions={[5, 10, 25, 50]}
              labelRowsPerPage="Elementos por página:"
              labelDisplayedRows={({ from, to, count }) => 
                `${from}-${to} de ${count !== -1 ? count : `más de ${to}`}`
              }
            />
          </>
        )}
      </Paper>

      {/* Botón flotante para agregar */}
      <AddButton onClick={onAddProvider}>
        <AddIcon />
      </AddButton>

      {/* Dialog de confirmación para eliminar */}
      <Dialog
        open={deleteDialog.open}
        onClose={handleDeleteCancel}
        aria-labelledby="delete-dialog-title"
      >
        <DialogTitle id="delete-dialog-title">
          Confirmar eliminación
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            ¿Estás seguro de que deseas eliminar al proveedor "{deleteDialog.providerName}"?
            Esta acción no se puede deshacer.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} color="primary">
            Cancelar
          </Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">
            Eliminar
          </Button>
        </DialogActions>
      </Dialog>
    </ProvidersContainer>
  );
};

export default ProvidersList;